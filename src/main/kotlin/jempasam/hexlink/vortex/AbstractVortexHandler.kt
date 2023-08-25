package jempasam.hexlink.vortex

import com.google.gson.JsonObject
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.utils.asSpirit
import jempasam.hexlink.utils.read
import net.minecraft.server.world.ServerWorld

abstract class AbstractVortexHandler(private var catalyzer: List<Spirit>, var output: List<Spirit>): CatalyzedVortexHandler {

    constructor(obj: JsonObject): this(
            obj.get("catalyzer")?.asJsonArray?.read { it.asSpirit() } ?: listOf(),
            obj.get("result")?.asJsonArray?.read { it.asSpirit() } ?: listOf()
    )

    override fun getCatalyzer(): List<Spirit> = catalyzer

    final override fun findRecipe(ingredients: List<Spirit>, world: ServerWorld): HexVortexHandler.Recipe? {
        if(ingredients.size>=catalyzer.size){
            for(i in 0..<catalyzer.size){
                if(ingredients[i]!=catalyzer[i])return null
            }
            return findRealRecipe(ingredients.subList(catalyzer.size,ingredients.size), world)
        }
        return null
    }

    abstract fun findRealRecipe(ingredients: List<Spirit>, world: ServerWorld): Recipe?

    final override fun getRecipesExamples(): Sequence<Pair<List<Spirit>, List<Spirit>>> {
        return getRealRecipesExamples().map {
            val i=catalyzer.toMutableList()
            val o=it.second.toMutableList()
            i.addAll(it.first)
            o.addAll(output)
            i to o
        }
    }

    abstract fun getRealRecipesExamples(): Sequence<Pair<List<Spirit>, List<Spirit>>>

    abstract class Recipe(private val handler: AbstractVortexHandler): HexVortexHandler.Recipe{
        final override fun ingredientCount(): Int = handler.catalyzer.size + realIngredientCount()

        final override fun mix(ingredients: List<Spirit>): List<Spirit> {
            val result=realMix(ingredients.subList(handler.catalyzer.size, ingredients.size)).toMutableList()
            result.addAll(handler.output)
            return result
        }

        abstract fun realIngredientCount(): Int

        abstract fun realMix(ingredients: List<Spirit>): List<Spirit>
    }

}