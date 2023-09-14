package jempasam.hexlink.recipe.vortex

import com.google.gson.JsonObject
import jempasam.hexlink.spirit.ColorSpirit
import jempasam.hexlink.spirit.Spirit
import net.minecraft.recipe.RecipeManager
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.ColorHelper

class ColorVortexHandler : AbstractVortexHandler {

    constructor(catalyzer: List<Spirit>, output: List<Spirit>): super(catalyzer, output)

    constructor(obj: JsonObject): super(obj)


    override fun findRealRecipe(ingredients: List<Spirit>, world: ServerWorld): AbstractVortexHandler.Recipe? {
        if(ingredients.size>=2 && ingredients[0] is ColorSpirit && ingredients[1] is ColorSpirit){
            return Recipe(this)
        }
        return null
    }

    override fun getRealRecipesExamples(manager: RecipeManager): Sequence<Pair<List<HexVortexHandler.Ingredient>, List<Spirit>>> {
        return sequenceOf(
            listOf(
                HexVortexHandler.Ingredient(ColorSpirit(0xFF0000)),
                HexVortexHandler.Ingredient(ColorSpirit(0x00FF00))
            ) to listOf(ColorSpirit(0x888800)),
            listOf(
                HexVortexHandler.Ingredient(ColorSpirit(0xFF0000)),
                HexVortexHandler.Ingredient(ColorSpirit(0x0000FF))
            ) to listOf(ColorSpirit(0x880088)),
            listOf(
                HexVortexHandler.Ingredient(ColorSpirit(0xFF0000)),
                HexVortexHandler.Ingredient(ColorSpirit(0x000000))
            ) to listOf(ColorSpirit(0x880000)),
            listOf(
                HexVortexHandler.Ingredient(ColorSpirit(0x0000FF)),
                HexVortexHandler.Ingredient(ColorSpirit(0x00FF00))
            ) to listOf(ColorSpirit(0x008888))
        )
    }

    class Recipe(val handler: ColorVortexHandler): AbstractVortexHandler.Recipe(handler){
        override fun realIngredientCount(): Int = 2

        override fun realMix(ingredients: List<Spirit>): List<Spirit> {
            return listOf(
                ColorSpirit(ColorHelper.Argb.mixColor((ingredients[0] as ColorSpirit).getColor(), (ingredients[1] as ColorSpirit).getColor()))
            )
        }
    }

    object PARSER: HexVortexHandler.Parser<ColorVortexHandler> {
        override fun serialize(json: JsonObject): ColorVortexHandler = ColorVortexHandler(json)
    }

}