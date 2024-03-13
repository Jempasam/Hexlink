package jempasam.hexlink.recipe.vortex

import com.google.gson.JsonObject
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.inout.SpiritHelper
import jempasam.hexlink.utils.getSpirit
import net.fabricmc.fabric.api.registry.FuelRegistry
import net.minecraft.recipe.RecipeManager
import net.minecraft.registry.Registries
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.JsonHelper.getFloat
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

    override fun getRealRecipesExamples(manager: RecipeManager): Sequence<Pair<List<HexVortexHandler.Ingredient>, List<Spirit>>> {
        return Registries.ITEM.entrySet.asSequence().mapNotNull {
            val item=it.value
            val cooktime=FuelRegistry.INSTANCE.get(item)
            if(cooktime!=null){
                val result= mutableListOf<Spirit>()
                val count=max(1,(cooktime/200*multiplier).toInt())
                for(i in 0..<count)result.add(burningResult)
                listOf(HexVortexHandler.Ingredient(SpiritHelper.asSpirit(item))) to result
            }
            else null
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

    object PARSER: HexVortexHandler.Parser<BurningVortexHandler> {
        override fun serialize(json: JsonObject): BurningVortexHandler = BurningVortexHandler(json)
    }

}