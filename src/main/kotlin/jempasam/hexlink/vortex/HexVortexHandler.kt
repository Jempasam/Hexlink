package jempasam.hexlink.vortex

import jempasam.hexlink.spirit.Spirit
import net.minecraft.server.world.ServerWorld

interface HexVortexHandler {

    fun findRecipe(ingredients: List<Spirit>, world: ServerWorld): Recipe?

    interface Recipe{
        fun mix(ingredients: List<Spirit>): List<Spirit>
        fun ingredientCount(): Int
        fun test(ingredients: List<Spirit>): Boolean
    }
}