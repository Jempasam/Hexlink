package jempasam.hexlink.spirit.extracter

import com.google.gson.JsonElement
import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.spirit.EntitySpirit
import jempasam.hexlink.spirit.extracter.loaders.SpiritExtractorLoader
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.tag.TagKey
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object EntityExtractor : SpiritExtractor<EntitySpirit> {

    val NOT_EXTRACTABLE= TagKey.of(Registry.ENTITY_TYPE_KEY, Identifier(HexlinkMod.MODID, "not_extractable"))

    override fun extract(target: Entity): SpiritExtractor.ExtractionResult<EntitySpirit> {
        return result(EntitySpirit(target.type),1)
    }

    override fun canExtract(target: Entity): Boolean {
        if(target.type.isSummonable && (target !is LivingEntity || target.health<=4.0f) && !target.isRemoved && !target.type.isIn(NOT_EXTRACTABLE)){
            return true
        }
        return false
    }

    override fun consume(target: Entity) {
        target.kill()
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