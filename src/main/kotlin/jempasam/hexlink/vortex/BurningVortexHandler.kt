package jempasam.hexlink.vortex

import jempasam.hexlink.spirit.BlockSpirit
import jempasam.hexlink.spirit.ItemSpirit
import jempasam.hexlink.spirit.Spirit
import net.fabricmc.fabric.api.registry.FuelRegistry
import net.minecraft.block.Blocks
import net.minecraft.item.Item
import net.minecraft.server.world.ServerWorld

class BurningVortexHandler(val catalzer: Spirit, val multiplier: Float) : CatalyzedVortexHandler{

    override fun getCatalyzer(): Spirit = catalzer

    override fun findRecipe(ingredients: List<Spirit>, world: ServerWorld): HexVortexHandler.Recipe? {
        if(ingredients.size>=2){
            val first=ingredients[0]
            val ingredient=ingredients[1]
            if(first==catalzer && ingredient is ItemSpirit){
                val item=ingredient.item
                val result=FuelRegistry.INSTANCE.get(item)
                if(result!=null){
                    return Recipe(item, result, this)
                }
            }
        }
        return null
    }

    class Recipe(val burned: Item, val fuel_time: Int, val handler: BurningVortexHandler): HexVortexHandler.Recipe{
        override fun test(ingredients: List<Spirit>): Boolean {
            val first=ingredients[0]
            val ingredient=ingredients[1]
            if(first==handler.catalzer && ingredient is ItemSpirit && ingredient.item==burned){
                return true
            }
            return false
        }

        override fun ingredientCount(): Int = 2

        override fun mix(ingredients: List<Spirit>): List<Spirit> {
            if(fuel_time==0)return listOf()
            else{
                val maxi=Math.max(1,(fuel_time/200*handler.multiplier).toInt())
                val ret= mutableListOf<Spirit>()
                for(i in 0..<maxi)ret.add(BlockSpirit(Blocks.FIRE))
                return ret
            }
        }
    }
}