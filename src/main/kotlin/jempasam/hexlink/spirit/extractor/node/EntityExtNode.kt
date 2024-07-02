package jempasam.hexlink.spirit.extractor.node

import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import jempasam.hexlink.spirit.EntitySpirit
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity

object EntityExtNode : ExtractionNode {
    override fun filter(source: ExtractionNode.Source): ExtractionNode.Source {
        val target=source.entity
        if( target.type.isSummonable
            && (target !is LivingEntity || target.health<=4.0f)
            && !target.isRemoved
            && target.type.isSaveable)
        {
            return source.with {
                spirit=EntitySpirit(target.type)
                consumer={ target.remove(Entity.RemovalReason.KILLED) }
            }
        }
        return source
    }

    object Parser: ExtractionNode.Parser<EntityExtNode> {
        override fun parse(obj: JsonObject): EntityExtNode = EntityExtNode
    }

    val CODEC= Codec.unit(EntityExtNode)
}