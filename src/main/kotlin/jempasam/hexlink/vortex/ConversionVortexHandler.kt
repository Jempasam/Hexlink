package jempasam.hexlink.vortex

import com.google.gson.JsonObject
import jempasam.hexlink.spirit.Spirit
import net.minecraft.server.world.ServerWorld

class ConversionVortexHandler : AbstractVortexHandler{


    constructor(catalyzer: List<Spirit>, output: List<Spirit>, burning_result: Spirit, multiplier: Float)
            : super(catalyzer, output)

    constructor(obj: JsonObject)
            : super(obj)

    override fun findRealRecipe(ingredients: List<Spirit>, world: ServerWorld): AbstractVortexHandler.Recipe? {
        return Recipe(this)
    }

    override fun getRealRecipesExamples(): Sequence<Pair<List<Spirit>, List<Spirit>>> {
        return sequenceOf( listOf<Spirit>() to listOf() )
    }

    class Recipe(handler: ConversionVortexHandler): AbstractVortexHandler.Recipe(handler){
        override fun realIngredientCount(): Int = 1

        override fun realMix(ingredients: List<Spirit>): List<Spirit> = listOf()
    }

    object SERIALIZER: HexVortexHandler.Serializer<ConversionVortexHandler> {
        override fun serialize(json: JsonObject): ConversionVortexHandler = ConversionVortexHandler(json)
    }

}