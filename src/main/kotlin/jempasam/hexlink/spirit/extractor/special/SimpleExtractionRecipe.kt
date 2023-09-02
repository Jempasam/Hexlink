package jempasam.hexlink.spirit.extractor.special

import jempasam.hexlink.spirit.EntitySpirit
import jempasam.hexlink.spirit.extractor.ExtractionCondition
import jempasam.hexlink.spirit.extractor.ExtractionRecipe
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.predicate.entity.EntityPredicate
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld

class SimpleExtractionRecipe(val predicate: EntityPredicate, val maxHealth: Float, val count: Int) : ExtractionRecipe {
    override fun conditions(): Set<ExtractionCondition> = setOf(
        EntityCondition(predicate)
    )

    override fun get(caster: ServerPlayerEntity?, entity: Entity): ExtractionRecipe.Result? {
        if(     entity.type.isSummonable
                && (entity !is LivingEntity || entity.health<=maxHealth)
                && !entity.isRemoved  ){
            return ExtractionRecipe.Result{
                entity.kill()
                EntitySpirit(entity.type) to count
            }
        }
        return null
    }

    class EntityCondition(val predicate: EntityPredicate) : ExtractionCondition{
        override fun hashCode(): Int = predicate.hashCode()*76+5253

        override fun equals(other: Any?): Boolean = other is EntityCondition && other.predicate==predicate

        override fun test(caster: ServerPlayerEntity?, entity: Entity): Boolean {
            if(caster==null)return predicate.test(entity.world as ServerWorld, entity.pos, entity)
            else return predicate.test(caster,entity)
        }
    }
}