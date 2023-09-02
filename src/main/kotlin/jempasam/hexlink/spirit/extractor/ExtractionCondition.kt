package jempasam.hexlink.spirit.extractor

import com.google.gson.JsonObject
import net.minecraft.entity.Entity
import net.minecraft.server.network.ServerPlayerEntity

interface ExtractionCondition {
    override fun hashCode(): Int

    override fun equals(other: Any?): Boolean

    fun test(caster: ServerPlayerEntity?, entity: Entity): Boolean

    fun interface Parser{
        fun parse(obj: JsonObject): ExtractionCondition
    }
}