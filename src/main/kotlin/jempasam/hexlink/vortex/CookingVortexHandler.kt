package jempasam.hexlink.vortex

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
import kotlin.math.min

class CookingVortexHandler : AbstractVortexHandler{

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


    private val recipe_manager=RecipeManager.createCachedMatchGetter(RecipeType.SMELTING)

    override fun findRealRecipe(ingredients: List<Spirit>, world: ServerWorld): AbstractVortexHandler.Recipe? {
        if(ingredients.size>=1){
            val ingredient=ingredients[1]
            val item= SpiritHelper.asItem(ingredient)
            if(item!=null){
                val inventory=SimpleInventory(1)
                inventory.setStack(0, item.defaultStack)
                val cooking_recipe=recipe_manager.getFirstMatch(inventory,world)
                if(cooking_recipe.isPresent){
                    val result=cooking_recipe.get().craft(inventory)
                    if(!result.isEmpty){
                        return Recipe(result.item, min(1,(result.count*multiplier).toInt()), this, world)
                    }
                }
            }
        }
        return null
    }

    override fun getRealRecipesExamples(): Sequence<Pair<List<Spirit>, List<Spirit>>> = sequenceOf()

    class Recipe(val item: Item, val count: Int, handler: CookingVortexHandler, val world: ServerWorld): AbstractVortexHandler.Recipe(handler){
        override fun realIngredientCount(): Int = 1

        override fun realMix(ingredients: List<Spirit>): List<Spirit> {
            val ret= mutableListOf<Spirit>()
            for(i in 0..<count)ret.add(ItemSpirit(item))
            return ret
        }

    }

    object SERIALIZER: HexVortexHandler.Serializer<CookingVortexHandler> {
        override fun serialize(json: JsonObject): CookingVortexHandler = CookingVortexHandler(json)
    }
}