package jempasam.hexlink.recipe.vortex

import com.google.gson.JsonObject
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.inout.SpiritHelper
import jempasam.hexlink.utils.getSpirit
import net.minecraft.block.ComposterBlock
import net.minecraft.recipe.RecipeManager
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.JsonHelper.getFloat
import kotlin.math.max

class CompostingVortexHandler : AbstractVortexHandler {

    val multiplier: Float
    val compostingResult: Spirit


    constructor(catalyzer: List<Spirit>, output: List<Spirit>, composting_result: Spirit, multiplier: Float)
            : super(catalyzer, output)
    {
        this.multiplier=multiplier
        this.compostingResult=composting_result
    }

    constructor(obj: JsonObject)
            : super(obj)
    {
        this.compostingResult=obj.getSpirit("composting_result")
        this.multiplier=getFloat(obj,"multiplier",1.0f)
    }


    override fun findRealRecipe(ingredients: List<Spirit>, world: ServerWorld): AbstractVortexHandler.Recipe? {
        if(ingredients.isNotEmpty()){
            val ingredient=ingredients[0]
            val item=SpiritHelper.asItem(ingredient)
            if(item!=null){
                val count=ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.getOrElse(item) { -1.0f }
                if(count!=-1.0f){
                    return Recipe(max(1, (count.toFloat()*multiplier).toInt()), this)
                }
            }
        }
        return null
    }

    override fun getRealRecipesExamples(manager: RecipeManager): Sequence<Pair<List<HexVortexHandler.Ingredient>, List<Spirit>>> {
        return ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.asSequence().map {
            val result= mutableListOf<Spirit>()
            val count= max(1, (it.value*multiplier).toInt())
            for(i in 0..<count)result.add(compostingResult)
            listOf(HexVortexHandler.Ingredient(SpiritHelper.asSpirit(it.key.asItem()))) to result
        }
    }

    class Recipe(val count: Int, val handler: CompostingVortexHandler): AbstractVortexHandler.Recipe(handler){
        override fun realIngredientCount(): Int = 1

        override fun realMix(ingredients: List<Spirit>): List<Spirit> {
            if(count==0)return listOf()
            else{
                val ret= mutableListOf<Spirit>()
                for(i in 0..<count)ret.add(handler.compostingResult)
                return ret
            }
        }
    }

    object PARSER: HexVortexHandler.Parser<CompostingVortexHandler> {
        override fun serialize(json: JsonObject): CompostingVortexHandler = CompostingVortexHandler(json)
    }

}