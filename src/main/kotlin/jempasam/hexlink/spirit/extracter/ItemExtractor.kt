package jempasam.hexlink.spirit.extracter

import jempasam.hexlink.spirit.ItemSpirit
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

object ItemExtractor : SpiritExtractor<ItemSpirit> {

    override fun extract(target: Entity): SpiritExtractor.ExtractionResult<ItemSpirit> {
        target as ItemEntity
        return result(ItemSpirit(target.stack.item),target.stack.count)
    }

    override fun canExtract(target: Entity): Boolean {
        return target is ItemEntity
    }

    override fun getExtractedName(): Text {
        return Text.translatable("hexlink.extractor.item")
    }

    override fun getColor(): Int {
        return DyeColor.BROWN.fireworkColor
    }
}