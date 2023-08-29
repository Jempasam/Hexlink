package jempasam.hexlink.spirit.extracter

import com.google.gson.JsonElement
import jempasam.hexlink.spirit.BlockSpirit
import jempasam.hexlink.spirit.StackHelper
import jempasam.hexlink.spirit.extracter.loaders.SpiritExtractorLoader
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

object BlockExtractor : SpiritExtractor<BlockSpirit> {

    override fun extract(caster: PlayerEntity?, target: Entity): SpiritExtractor.ExtractionResult<BlockSpirit> {
        val worldstack= StackHelper.stack(caster, target)
        val stack= worldstack?.stack
        if(stack!=null && stack.item is BlockItem && !stack.isIn(ItemExtractor.NOT_EXTRACTABLE)){
            return SpiritExtractor.ExtractionResult(
                    BlockSpirit((stack.item as BlockItem).block),
                    stack.count
            ) { worldstack.killer() }
        }
        else return noResult()
    }

    override fun getName(): Text = Text.translatable("hexlink.extractor.block")

    override fun getColor(): Int = DyeColor.LIGHT_GRAY.fireworkColor

    object Serializer : SpiritExtractorLoader<BlockExtractor>{
        override fun load(element: JsonElement): BlockExtractor {
            return BlockExtractor
        }
    }
}