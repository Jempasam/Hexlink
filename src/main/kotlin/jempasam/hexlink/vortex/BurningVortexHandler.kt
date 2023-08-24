package jempasam.hexlink.vortex

import com.google.gson.JsonObject
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.inout.SpiritHelper
import jempasam.hexlink.utils.getSpirit
import net.fabricmc.fabric.api.registry.FuelRegistry
import net.minecraft.item.Item
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.JsonHelper.getFloat
import kotlin.math.max

class BurningVortexHandler(val catalzer: Spirit, val result: Spirit, val multiplier: Float) : CatalyzedVortexHandler{

    override fun getCatalyzer(): Spirit = catalzer

    override fun findRecipe(ingredients: List<Spirit>, world: ServerWorld): HexVortexHandler.Recipe? {
        if(ingredients.size>=2){
            val first=ingredients[0]
            val ingredient=ingredients[1]
            val item=SpiritHelper.asItem(ingredient)
            if(first==catalzer && item!=null){
                val result=FuelRegistry.INSTANCE.get(item)
                if(result!=null){
                    return Recipe(item, result, this)
                }
            }
        }
        return null
    }

    class Recipe(val burned: Item, val fuel_time: Int, val handler: BurningVortexHandler): HexVortexHandler.Recipe{
        override fun ingredientCount(): Int = 2

        override fun mix(ingredients: List<Spirit>): List<Spirit> {
            if(fuel_time==0)return listOf()
            else{
                val maxi= max(1,(fuel_time/200*handler.multiplier).toInt())
                val ret= mutableListOf<Spirit>()
                for(i in 0..<maxi)ret.add(handler.result)
                return ret
            }
        }
    }

    object SERIALIZER: HexVortexHandler.Serializer<BurningVortexHandler>{
        override fun serialize(json: JsonObject): BurningVortexHandler {
            return BurningVortexHandler(
                    json.getSpirit("catalyzer"),
                    json.getSpirit("result"),
                    getFloat(json,"multiplier",1.0f)
            )
        }
    }
}