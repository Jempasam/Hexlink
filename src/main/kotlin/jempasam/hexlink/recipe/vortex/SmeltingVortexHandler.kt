package jempasam.hexlink.recipe.vortex

import com.google.gson.JsonObject
import jempasam.hexlink.spirit.ItemSpirit
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.inout.SpiritHelper
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.Item
import net.minecraft.recipe.RecipeManager
import net.minecraft.recipe.RecipeType
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.JsonHelper
import kotlin.math.max
import kotlin.math.min

class SmeltingVortexHandler : AbstractVortexHandler {

    val multiplier: Float
    constructor(catalyzer: List<Spirit>, output: List<Spirit>, multiplier: Float)
            : super(catalyzer, output)
    {
        this.multiplier=multiplier
    }

    constructor(obj: JsonObject)
            : super(obj)
    {
        this.multiplier=JsonHelper.getFloat(obj, "multiplier", 1.0f)
    }


    private val recipeManager=RecipeManager.createCachedMatchGetter(RecipeType.SMELTING)

    override fun findRealRecipe(ingredients: List<Spirit>, world: ServerWorld): AbstractVortexHandler.Recipe? {
        if(ingredients.isNotEmpty()){
            val ingredient=ingredients[0]
            val item= SpiritHelper.asItem(ingredient)
            if(item!=null){
                val inventory=SimpleInventory(1)
                inventory.setStack(0, item.defaultStack)
                val cookingRecipe=recipeManager.getFirstMatch(inventory,world)
                if(cookingRecipe.isPresent){
                    val result=cookingRecipe.get().craft(inventory)
                    if(!result.isEmpty){
                        return Recipe(result.item, min(1,(result.count*multiplier).toInt()), this, world)
                    }
                }
            }
        }
        return null
    }

    override fun getRealRecipesExamples(manager: RecipeManager): Sequence<Pair<List<HexVortexHandler.Ingredient>, List<Spirit>>>{
        return sequence {
            for (recipe in manager.listAllOfType(RecipeType.SMELTING))yield(
                listOf(HexVortexHandler.Ingredient(recipe.ingredients[0])) to
                        List(max(1,(multiplier*recipe.output.count).toInt())){ SpiritHelper.asSpirit(recipe.output.item)}
            )
        }
    }

    class Recipe(val item: Item, val count: Int, handler: SmeltingVortexHandler, val world: ServerWorld): AbstractVortexHandler.Recipe(handler){
        override fun realIngredientCount(): Int = 1

        override fun realMix(ingredients: List<Spirit>): List<Spirit> {
            val ret= mutableListOf<Spirit>()
            for(i in 0..<count)ret.add(ItemSpirit(item))
            return ret
        }

    }

    object PARSER: HexVortexHandler.Parser<SmeltingVortexHandler> {
        override fun serialize(json: JsonObject): SmeltingVortexHandler = SmeltingVortexHandler(json)
    }
}