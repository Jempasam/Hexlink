package jempasam.hexlink.spirit.extracter

import jempasam.hexlink.spirit.BlockSpirit
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.item.BlockItem
import net.minecraft.text.Text

object BlockExtractor : SpiritExtractor<BlockSpirit> {
    override fun extract(target: Entity): SpiritExtractor.ExtractionResult<BlockSpirit> {
        target as ItemEntity
        return result(BlockSpirit((target.stack.item as BlockItem).block), target.stack.count)
    }

    override fun canExtract(target: Entity): Boolean {
        if(target is ItemEntity && target.stack.item is BlockItem){
            return true
        }
        return false
    }

    override fun getExtractedName(): Text {
        return Text.translatable("hexlink.extractor.block")
    }
}