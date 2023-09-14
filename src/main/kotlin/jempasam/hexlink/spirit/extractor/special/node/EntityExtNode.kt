package jempasam.hexlink.spirit.extractor.special.node

import com.google.gson.JsonObject
import jempasam.hexlink.spirit.EntitySpirit
import jempasam.hexlink.spirit.extractor.EntityExtractor
import net.minecraft.entity.LivingEntity

object EntityExtNode : ExtractionNode{
    override fun filter(source: ExtractionNode.Source): ExtractionNode.Source {
        val target=source.entity
        if( target.type.isSummonable
            && (target !is LivingEntity || target.health<=4.0f)
            && !target.isRemoved && !target.type.isIn(EntityExtractor.NOT_EXTRACTABLE) )
        {
            return source.with {
                spirit=EntitySpirit(target.type)
                consumer={ target.kill() }
            }
        }
        return source
    }

    object Parser: ExtractionNode.Parser<EntityExtNode>{
        override fun parse(obj: JsonObject): EntityExtNode = EntityExtNode
    }
}