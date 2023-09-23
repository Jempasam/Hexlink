package jempasam.hexlink.recipe.vortex

import com.google.gson.JsonObject
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.utils.asSpirit
import jempasam.hexlink.utils.read
import net.minecraft.recipe.RecipeManager
import net.minecraft.server.world.ServerWorld

abstract class AbstractVortexHandler(private var catalyzer: List<Spirit>, var output: List<Spirit>): CatalyzedVortexHandler {

    constructor(obj: JsonObject): this(
            obj.get("catalyzer")?.asJsonArray?.read { it.asSpirit() } ?: listOf(),
            obj.get("result")?.asJsonArray?.read { it.asSpirit() } ?: listOf()
    )

    override fun getCatalyzer(): List<Spirit> = catalyzer

    final override fun findRecipe(ingredients: Collection<Spirit>, world: ServerWorld): HexVortexHandler.Recipe? {
        if(ingredients.size>=catalyzer.size){
            val it=ingredients.iterator()
            for(ct in catalyzer){
                if(it.next()!=ct)return null
            }
            return findRealRecipe(ingredients.drop(catalyzer.size), world)
        }
        return null
    }

    abstract fun findRealRecipe(ingredients: Collection<Spirit>, world: ServerWorld): Recipe?

    final override fun getRecipesExamples(manager: RecipeManager): Sequence<Pair<List<HexVortexHandler.Ingredient>, List<Spirit>>> {
        return getRealRecipesExamples(manager).map {
            val i=catalyzer.asSequence().map { HexVortexHandler.Ingredient(it) }.toMutableList()
            val o=it.second.toMutableList()
            i.addAll(it.first)
            o.addAll(output)
            i to o
        }
    }

    abstract fun getRealRecipesExamples(manager: RecipeManager): Sequence<Pair<List<HexVortexHandler.Ingredient>, List<Spirit>>>

    abstract class Recipe(private val handler: AbstractVortexHandler): HexVortexHandler.Recipe {
        final override fun ingredientCount(): Int = handler.catalyzer.size + realIngredientCount()

        final override fun mix(ingredients: Collection<Spirit>): List<Spirit> {
            val result=realMix(ingredients.drop(handler.catalyzer.size)).toMutableList()
            result.addAll(handler.output)
            return result
        }

        abstract fun realIngredientCount(): Int

        abstract fun realMix(ingredients: Collection<Spirit>): List<Spirit>
    }

}