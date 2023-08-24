package jempasam.hexlink.vortex

import com.google.gson.JsonObject
import jempasam.hexlink.spirit.Spirit
import net.minecraft.server.world.ServerWorld

interface HexVortexHandler {

    fun findRecipe(ingredients: List<Spirit>, world: ServerWorld): Recipe?

    fun getRecipesExamples(): Sequence<Pair<List<Spirit>,List<Spirit>>> = sequenceOf()

    interface Recipe{
        fun mix(ingredients: List<Spirit>): List<Spirit>
        fun ingredientCount(): Int
    }

    interface Serializer<T: HexVortexHandler>{
        fun serialize(json: JsonObject): T
    }
}