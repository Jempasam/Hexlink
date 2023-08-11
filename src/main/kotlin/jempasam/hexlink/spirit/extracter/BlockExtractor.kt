package jempasam.hexlink.spirit.extracter

import com.google.gson.JsonElement
import jempasam.hexlink.spirit.BlockSpirit
import jempasam.hexlink.spirit.extracter.loaders.SpiritExtractorLoader
import net.minecraft.entity.Entity
import net.minecraft.item.BlockItem
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

object BlockExtractor : SpiritExtractor<BlockSpirit> {
    override fun canExtract(target: Entity): Boolean {
        val stack= ExtractorHelper.stack(target)
        if(stack!=null && stack.item is BlockItem && !stack.isIn(ItemExtractor.NOT_EXTRACTABLE)){
            return true
        }
        return false
    }

    override fun extract(target: Entity): SpiritExtractor.ExtractionResult<BlockSpirit> {
        val stack= ExtractorHelper.stackOrThrow(target)
        return result(BlockSpirit((stack.item as BlockItem).block), stack.count)
    }

    override fun consume(target: Entity) {
        ExtractorHelper.killStack(target)
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