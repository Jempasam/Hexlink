package jempasam.hexlink.recipe.vortex

import com.google.gson.JsonObject
import jempasam.hexlink.spirit.ColorSpirit
import jempasam.hexlink.spirit.Spirit
import net.minecraft.recipe.RecipeManager
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.ColorHelper.Argb.*

class ColorVortexHandler : AbstractVortexHandler {

    constructor(catalyzer: List<Spirit>, output: List<Spirit>): super(catalyzer, output)

    constructor(obj: JsonObject): super(obj)

    override fun findRealRecipe(ingredients: Collection<Spirit>, world: ServerWorld): AbstractVortexHandler.Recipe? {
        val it=ingredients.iterator()
        if(ingredients.size>=2 && it.next() is ColorSpirit && it.next() is ColorSpirit){
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

        @OptIn(ExperimentalStdlibApi::class)
        override fun realMix(ingredients: Collection<Spirit>): List<Spirit> {
            val it=ingredients.iterator()
            val color1=(it.next() as ColorSpirit).getColor()
            val color2=(it.next() as ColorSpirit).getColor()
            return listOf(
                ColorSpirit(
                    getArgb(
                        0,
                        (getRed(color1) + getRed(color2))/2,
                        (getGreen(color1) + getGreen(color2))/2,
                        (getBlue(color1) + getBlue(color2))/2
                    )
                )
            )
        }
    }

    override val parser get() = PARSER

    object PARSER: HexVortexHandler.Parser<ColorVortexHandler> {
        override fun parse(json: JsonObject): ColorVortexHandler = ColorVortexHandler(json)
    }

}