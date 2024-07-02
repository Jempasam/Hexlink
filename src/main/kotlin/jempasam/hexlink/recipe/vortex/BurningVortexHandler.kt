package jempasam.hexlink.recipe.vortex

import com.google.gson.JsonObject
import jempasam.hexlink.recipe.vortex.HexVortexHandler.Ingredient
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.inout.SpiritHelper
import jempasam.hexlink.utils.addSpirit
import jempasam.hexlink.utils.getSpirit
import net.fabricmc.fabric.api.registry.FuelRegistry
import net.minecraft.recipe.RecipeManager
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.JsonHelper.getFloat
import net.minecraft.util.registry.Registry
import kotlin.math.max

class BurningVortexHandler : AbstractVortexHandler {

    val multiplier: Float
    val burningResult: Spirit


    constructor(catalyzer: List<Spirit>, output: List<Spirit>, burningResult1: Spirit, multiplier: Float)
            : super(catalyzer, output)
    {
        this.multiplier=multiplier
        this.burningResult=burningResult1
    }

    constructor(obj: JsonObject)
            : super(obj)
    {
        this.burningResult=obj.getSpirit("burning_result")
        this.multiplier=getFloat(obj,"multiplier",1.0f)
    }

    override fun serialize(obj: JsonObject){
        super.serialize(obj)
        obj.addProperty("multiplier", multiplier)
        obj.addSpirit("burning_result", burningResult)
    }


    override fun findRealRecipe(ingredients: Collection<Spirit>, world: ServerWorld): AbstractVortexHandler.Recipe? {
        if(ingredients.isNotEmpty()){
            val it=ingredients.iterator()
            val ingredient=it.next()
            val item=SpiritHelper.asItem(ingredient)
            if(item!=null){
                val result=FuelRegistry.INSTANCE.get(item)
                if(result!=null){
                    return Recipe(result, this)
                }
            }
        }
        return null
    }

    override fun getRealRecipesExamples(manager: RecipeManager): Sequence<Pair<List<Ingredient>, List<Spirit>>> {
        return sequence {
            Registry.ITEM.entrySet.asSequence()
                // Associate cookable item to result count
                .mapNotNull { item->
                    val cooktime=FuelRegistry.INSTANCE.get(item.value)
                    if(cooktime==null) null
                    else max(1,(cooktime/200*multiplier).toInt()) to item.value
                }
                .groupBy { it.first }

                // Create the recipe
                .entries.forEach { (result_count,items) ->
                    val ingredients= listOf(Ingredient(items.asSequence().map{ SpiritHelper.asSpirit(it.second) }, this::class.hashCode()+result_count))
                    val results= List(result_count){burningResult}
                    yield(ingredients to results)
                }
        }
    }

    class Recipe(val fuel_time: Int, val handler: BurningVortexHandler): AbstractVortexHandler.Recipe(handler){
        override fun realIngredientCount(): Int = 1

        override fun realMix(ingredients: Collection<Spirit>): List<Spirit> {
            if(fuel_time==0)return listOf()
            else{
                val maxi= max(1,(fuel_time/200*handler.multiplier).toInt())
                val ret= mutableListOf<Spirit>()
                for(i in 0..<maxi)ret.add(handler.burningResult)
                return ret
            }
        }
    }

    override val parser get() = PARSER

    object PARSER: HexVortexHandler.Parser<BurningVortexHandler> {
        override fun parse(json: JsonObject): BurningVortexHandler = BurningVortexHandler(json)
    }

}