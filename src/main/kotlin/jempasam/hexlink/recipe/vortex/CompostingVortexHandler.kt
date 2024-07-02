package jempasam.hexlink.recipe.vortex

import com.google.gson.JsonObject
import jempasam.hexlink.recipe.vortex.HexVortexHandler.Ingredient
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.inout.SpiritHelper
import jempasam.hexlink.utils.addSpirit
import jempasam.hexlink.utils.getSpirit
import net.minecraft.block.ComposterBlock
import net.minecraft.recipe.RecipeManager
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.JsonHelper.getFloat
import kotlin.math.ceil

class CompostingVortexHandler : AbstractVortexHandler {

    val multiplier: Float
    val compostingResult: Spirit


    constructor(catalyzer: List<Spirit>, output: List<Spirit>, composting_result: Spirit, multiplier: Float)
            : super(catalyzer, output)
    {
        this.multiplier=multiplier
        this.compostingResult=composting_result
    }

    constructor(obj: JsonObject) : super(obj)
    {
        this.compostingResult=obj.getSpirit("composting_result")
        this.multiplier=getFloat(obj,"multiplier",1.0f)
    }

    override fun serialize(obj: JsonObject) {
        super.serialize(obj)
        obj.addProperty("multiplier", multiplier)
        obj.addSpirit("composting_result", compostingResult)
    }

    private fun probaToCount(proba: Float) = ceil(1/proba*7).toInt()

    override fun findRealRecipe(ingredients: Collection<Spirit>, world: ServerWorld): AbstractVortexHandler.Recipe? {
        val it=ingredients.iterator()

        if(!it.hasNext()) return null
        val first=SpiritHelper.asItem(it.next()) ?: return null
        val proba=ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.getOrElse(first) { -1.0f }

        if(proba==-1.0f)return null
        val count=probaToCount(proba)
        for(i in 1..<count) if(!it.hasNext() || SpiritHelper.asItem(it.next())!=first) return null
        return Recipe(count, this)
    }

    override fun getRealRecipesExamples(manager: RecipeManager): Sequence<Pair<List<HexVortexHandler.Ingredient>, List<Spirit>>> {
        return sequence {
            ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.asSequence()
                // Associate item to input count
                .map { (item,increase)-> probaToCount(increase) to item.asItem() }
                .groupBy { it.first }
                // Create the recipe
                .entries.forEach { (input_count,items) ->
                    val ingredient=Ingredient(items.asSequence().map{ SpiritHelper.asSpirit(it.second) }, this::class.hashCode()+input_count)
                    val ingredients= List(input_count){ingredient}
                    val results= listOf(compostingResult)
                    yield(ingredients to results)
                }
        }
    }

    class Recipe(val price: Int, val handler: CompostingVortexHandler): AbstractVortexHandler.Recipe(handler){
        override fun realIngredientCount(): Int = price

        override fun realMix(ingredients: Collection<Spirit>): List<Spirit> {
            return listOf(handler.compostingResult)
        }
    }

    override val parser get() = PARSER

    object PARSER: HexVortexHandler.Parser<CompostingVortexHandler> {
        override fun parse(json: JsonObject): CompostingVortexHandler = CompostingVortexHandler(json)
    }

}