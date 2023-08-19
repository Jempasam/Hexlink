package jempasam.hexlink.vortex

import jempasam.hexlink.spirit.BlockSpirit
import jempasam.hexlink.spirit.ItemSpirit
import jempasam.hexlink.spirit.Spirit
import net.fabricmc.fabric.api.registry.FuelRegistry
import net.minecraft.block.Blocks
import net.minecraft.item.Item

class BurningVortexHandler : HexVortexHandler{
    override fun findRecipe(ingredients: List<Spirit>): HexVortexHandler.Recipe? {
        if(ingredients.isNotEmpty()){
            println("not empty")
            val ingredient=ingredients[0]
            if(ingredient is ItemSpirit){
                println("is item")
                val item=ingredient.item
                if(FuelRegistry.INSTANCE.get(item)!=null){
                    println("is fuel")
                    return Recipe(item)
                }
            }
        }
        return null
    }

    class Recipe(val burned: Item): HexVortexHandler.Recipe{
        override fun test(ingredients: List<Spirit>): Boolean {
            val ingredient=ingredients[0]
            println("test")
            if(ingredient is ItemSpirit && ingredient.item==burned){
                println("is burned")
                return true
            }
            return false
        }

        override fun ingredientCount(): Int = 1

        override fun mix(ingredients: List<Spirit>): List<Spirit> {
            val fuel_time=FuelRegistry.INSTANCE.get(burned) ?: 0
            println("fuel time of "+fuel_time)
            if(fuel_time==0)return listOf()
            else{
                val maxi=Math.max(1,fuel_time/200)
                val ret= mutableListOf<Spirit>()
                for(i in 0..<maxi)ret.add(BlockSpirit(Blocks.FIRE))
                return ret
            }
        }
    }
}