package jempasam.hexlink.vortex

import com.google.gson.JsonObject
import jempasam.hexlink.spirit.ItemSpirit
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.inout.SpiritHelper
import jempasam.hexlink.utils.getSpirit
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.Item
import net.minecraft.recipe.RecipeManager
import net.minecraft.recipe.RecipeType
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.JsonHelper
import kotlin.math.min

class CookingVortexHandler(val catlzer: Spirit, val multiplier: Float) : CatalyzedVortexHandler{

    private val recipe_manager=RecipeManager.createCachedMatchGetter(RecipeType.SMELTING)

    override fun getCatalyzer(): Spirit = catlzer

    override fun findRecipe(ingredients: List<Spirit>, world: ServerWorld): HexVortexHandler.Recipe? {
        if(ingredients.size>=2){
            val first=ingredients[0]
            val ingredient=ingredients[1]
            val item= SpiritHelper.asItem(ingredient)
            if(first==catlzer && item!=null){
                val inventory=SimpleInventory(1)
                inventory.setStack(0, item.defaultStack)
                val cooking_recipe=recipe_manager.getFirstMatch(inventory,world)
                if(cooking_recipe.isPresent){
                    val result=cooking_recipe.get().craft(inventory)
                    if(!result.isEmpty){
                        return Recipe(result.item, min(1,(result.count*multiplier).toInt()), world)
                    }
                }
            }
        }
        return null
    }

    class Recipe(val item: Item, val count: Int, val world: ServerWorld): HexVortexHandler.Recipe{
        override fun ingredientCount(): Int = 2


        override fun mix(ingredients: List<Spirit>): List<Spirit> {
            val ret= mutableListOf<Spirit>()
            for(i in 0..<count)ret.add(ItemSpirit(item))
            return ret
        }
    }

    object SERIALIZER: HexVortexHandler.Serializer<CookingVortexHandler> {
        override fun serialize(json: JsonObject): CookingVortexHandler {
            return CookingVortexHandler(
                    json.getSpirit("catalyzer"),
                    //json.getCookingRecipeType("recipe_type"),
                    JsonHelper.getFloat(json, "multiplier", 1.0f)
            )
        }
    }
}