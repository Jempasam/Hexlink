package jempasam.hexlink.spirit.extracter

import com.google.gson.JsonElement
import jempasam.hexlink.spirit.BlockSpirit
import jempasam.hexlink.spirit.StackHelper
import jempasam.hexlink.spirit.extracter.loaders.SpiritExtractorLoader
import net.minecraft.entity.Entity
import net.minecraft.item.BlockItem
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

object BlockExtractor : SpiritExtractor<BlockSpirit> {
    override fun canExtract(target: Entity): Boolean {
        val stack= StackHelper.stack(null, target)?.stack
        return stack!=null && stack.item is BlockItem && !stack.isIn(ItemExtractor.NOT_EXTRACTABLE)
    }

    override fun extract(target: Entity): SpiritExtractor.ExtractionResult<BlockSpirit> {
        val stack= StackHelper.stackOrThrow(null, target).stack
        return result(BlockSpirit((stack.item as BlockItem).block), stack.count)
    }

    override fun consume(target: Entity) {
        StackHelper.stackOrThrow(null, target).killer()
    }

    override fun getExtractedName(): Text {
        return Text.translatable("hexlink.extractor.block")
    }

    override fun getColor(): Int {
        return DyeColor.LIGHT_GRAY.fireworkColor
    }

    object Serializer : SpiritExtractorLoader<BlockExtractor>{
        override fun load(element: JsonElement): BlockExtractor {
            return BlockExtractor
        }
    }
}