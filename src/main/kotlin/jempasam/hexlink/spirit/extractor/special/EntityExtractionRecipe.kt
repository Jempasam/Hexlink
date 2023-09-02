package jempasam.hexlink.spirit.extractor.special

import jempasam.hexlink.spirit.EntitySpirit
import jempasam.hexlink.spirit.extractor.ExtractionCondition
import jempasam.hexlink.spirit.extractor.ExtractionRecipe
import net.minecraft.entity.Entity
import net.minecraft.server.network.ServerPlayerEntity

class EntityExtractionRecipe(val count: Int) : ExtractionRecipe {
    override fun conditions(): Set<ExtractionCondition> = setOf(Condition)

    override fun get(caster: ServerPlayerEntity?, entity: Entity): ExtractionRecipe.Result? {
        return ExtractionRecipe.Result{
            entity.kill()
            EntitySpirit(entity.type) to count
        }
    }

    object Condition : ExtractionCondition{
        override fun hashCode(): Int = super.hashCode()

        override fun equals(other: Any?): Boolean = super.equals(other)

        override fun test(caster: ServerPlayerEntity?, entity: Entity): Boolean
            = entity.type.isSummonable && !entity.isRemoved
    }
}