package jempasam.hexlink.spirit.extractor

import com.google.gson.JsonObject
import jempasam.hexlink.spirit.Spirit
import net.minecraft.entity.Entity
import net.minecraft.server.network.ServerPlayerEntity

interface ExtractionRecipe {

    fun get(caster: ServerPlayerEntity?, entity: Entity): Result?

    fun conditions(): Set<ExtractionCondition>

    fun interface Result{
        fun create(): Pair<Spirit,Int>
    }

    fun interface Parser{
        fun parse(obj: JsonObject): ExtractionRecipe
    }
}