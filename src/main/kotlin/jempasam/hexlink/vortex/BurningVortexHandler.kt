package jempasam.hexlink.vortex

import com.google.gson.JsonObject
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.inout.SpiritHelper
import jempasam.hexlink.utils.getSpirit
import net.fabricmc.fabric.api.registry.FuelRegistry
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.JsonHelper.getFloat
import net.minecraft.util.registry.Registry
import kotlin.math.max

class BurningVortexHandler : AbstractVortexHandler{

    val multiplier: Float
    val burning_result: Spirit


    constructor(catalyzer: List<Spirit>, output: List<Spirit>, burning_result: Spirit, multiplier: Float)
            : super(catalyzer, output)
    {
        this.multiplier=multiplier
        this.burning_result=burning_result
    }

    constructor(obj: JsonObject)
            : super(obj)
    {
        this.burning_result=obj.getSpirit("burning_result")
        this.multiplier=getFloat(obj,"multiplier",1.0f)
    }


    override fun findRealRecipe(ingredients: List<Spirit>, world: ServerWorld): AbstractVortexHandler.Recipe? {
        if(ingredients.size>=1){
            val ingredient=ingredients[0]
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

    override fun getRealRecipesExamples(): Sequence<Pair<List<Spirit>, List<Spirit>>> {
        return Registry.ITEM.entrySet.asSequence().mapNotNull {
            val item=it.value
            val cooktime=FuelRegistry.INSTANCE.get(item)
            if(cooktime!=null){
                val result= mutableListOf<Spirit>()
                val count=max(1,(cooktime/200*multiplier).toInt())
                for(i in 0..<count)result.add(burning_result)
                listOf(SpiritHelper.asSpirit(item)) to result
            }
            else null
        }
    }

    class Recipe(val fuel_time: Int, val handler: BurningVortexHandler): AbstractVortexHandler.Recipe(handler){
        override fun realIngredientCount(): Int = 1

        override fun realMix(ingredients: List<Spirit>): List<Spirit> {
            if(fuel_time==0)return listOf()
            else{
                val maxi= max(1,(fuel_time/200*handler.multiplier).toInt())
                val ret= mutableListOf<Spirit>()
                for(i in 0..<maxi)ret.add(handler.burning_result)
                return ret
            }
        }
    }

    object SERIALIZER: HexVortexHandler.Serializer<BurningVortexHandler> {
        override fun serialize(json: JsonObject): BurningVortexHandler = BurningVortexHandler(json)
    }

}