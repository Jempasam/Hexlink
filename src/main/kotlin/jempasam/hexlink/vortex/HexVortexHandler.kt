package jempasam.hexlink.vortex

import jempasam.hexlink.spirit.Spirit

interface HexVortexHandler {

    fun findRecipe(ingredients: List<Spirit>): Recipe?

    interface Recipe{
        fun mix(ingredients: List<Spirit>): List<Spirit>
        fun ingredientCount(): Int
        fun test(ingredients: List<Spirit>): Boolean
    }
}