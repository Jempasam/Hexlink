package jempasam.hexlink.recipe.vortex

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.utils.asSpirit
import jempasam.hexlink.utils.read
import jempasam.hexlink.utils.toJSON
import net.minecraft.recipe.RecipeManager
import net.minecraft.server.world.ServerWorld

open class SimpleVortexHandler(private var catalyzer: List<Spirit>, var output: List<Spirit>): CatalyzedVortexHandler {

    constructor(obj: JsonObject): this(
            obj.get("input")?.asJsonArray?.read { it.asSpirit() } ?: listOf(),
            obj.get("result")?.asJsonArray?.read { it.asSpirit() } ?: listOf()
    )

    override fun serialize(json: JsonObject){
        json.add("input", JsonArray().apply { catalyzer.forEach { add(it.toJSON()) } })
        json.add("result", JsonArray().apply { output.forEach { add(it.toJSON()) } })
    }

    override fun getCatalyzer(): List<Spirit> = catalyzer

    override fun findRecipe(ingredients: Collection<Spirit>, world: ServerWorld): HexVortexHandler.Recipe? {
        if(ingredients.size>=catalyzer.size){
            val it=ingredients.iterator()
            for(ct in catalyzer){
                if(it.next()!=ct)return null
            }
            return Recipe(this)
        }
        return null
    }

    override fun getRecipesExamples(manager: RecipeManager): Sequence<Pair<List<HexVortexHandler.Ingredient>, List<Spirit>>>
        = sequenceOf( catalyzer.asSequence().map { HexVortexHandler.Ingredient(it) }.toMutableList() to output)

    open class Recipe(private val handler: SimpleVortexHandler): HexVortexHandler.Recipe {
        final override fun ingredientCount(): Int = handler.catalyzer.size

        final override fun mix(ingredients: Collection<Spirit>): List<Spirit> = handler.output
    }

    override val parser get() = PARSER

    object PARSER: HexVortexHandler.Parser<SimpleVortexHandler> {
        override fun parse(json: JsonObject): SimpleVortexHandler = SimpleVortexHandler(json)
    }

}