package jempasam.hexlink.spirit.extracter

import jempasam.hexlink.spirit.BiomeSpirit
import jempasam.hexlink.spirit.SpiritHelper
import net.minecraft.entity.Entity
import net.minecraft.item.Items
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.math.BlockPos

object BiomeExtractor : SpiritExtractor<BiomeSpirit> {
    override fun getColor(): Int = DyeColor.GREEN.signColor

    override fun getExtractedName(): Text = Text.translatable("hexlink.extractor.biome")

    override fun canExtract(target: Entity): Boolean {
        val stack= SpiritHelper.stack(null, target)?.stack
        return stack!=null && stack.item==Items.DIAMOND
    }

    override fun extract(target: Entity): SpiritExtractor.ExtractionResult<BiomeSpirit> {
        val stack= SpiritHelper.stackOrThrow(null, target).stack
        val biome=target.world.getBiome(BlockPos(target.pos))
        return SpiritExtractor.ExtractionResult(BiomeSpirit(biome),stack.count)
    }

    override fun consume(target: Entity) {
        SpiritHelper.stackOrThrow(null, target).killer()
    }

}