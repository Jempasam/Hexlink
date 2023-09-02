package jempasam.hexlink.spirit.extractor.condition

import com.google.gson.JsonParseException
import jempasam.hexlink.spirit.extractor.ExtractionCondition
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.predicate.entity.EntityPredicate
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.JsonHelper
import java.util.*

class PredicateExtractionCondition(val predicate: EntityPredicate, val maxHealth: Float) : ExtractionCondition {
    override fun hashCode(): Int = Objects.hash(predicate.hashCode(), maxHealth, 623)

    override fun equals(other: Any?): Boolean
        = other is PredicateExtractionCondition && other.predicate==predicate && other.maxHealth==maxHealth

    override fun test(caster: ServerPlayerEntity?, entity: Entity): Boolean {
        return  (entity !is LivingEntity || entity.health<=maxHealth)
    }

    companion object val Parser= ExtractionCondition.Parser{obj ->
        PredicateExtractionCondition(
            obj.get("predicate")?.let { EntityPredicate.fromJson(it) } ?: throw JsonParseException("Missing predicate"),
            JsonHelper.getFloat(obj, "maxHealth")
        )
    }

}