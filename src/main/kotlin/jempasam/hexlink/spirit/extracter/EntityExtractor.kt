package jempasam.hexlink.spirit.extracter

import com.google.gson.JsonElement
import jempasam.hexlink.spirit.EntitySpirit
import jempasam.hexlink.spirit.extracter.loaders.SpiritExtractorLoader
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

object EntityExtractor : SpiritExtractor<EntitySpirit> {
    override fun extract(target: Entity): SpiritExtractor.ExtractionResult<EntitySpirit> {
        return result(EntitySpirit(target.type),1)
    }

    override fun canExtract(target: Entity): Boolean {
        if(target.type.isSummonable && (target !is LivingEntity || target.health<=4.0f)){
            return true
        }
        return false
    }

    override fun getExtractedName(): Text {
        return Text.translatable("hexlink.extractor.entity")
    }

    override fun getColor(): Int {
        return DyeColor.RED.fireworkColor
    }

    object Serializer : SpiritExtractorLoader<EntityExtractor> {
        override fun load(element: JsonElement): EntityExtractor {
            return EntityExtractor
        }
    }
}