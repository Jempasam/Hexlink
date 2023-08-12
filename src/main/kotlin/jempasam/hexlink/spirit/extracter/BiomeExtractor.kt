package jempasam.hexlink.spirit.extracter

import jempasam.hexlink.spirit.BiomeSpirit
import net.minecraft.entity.Entity
import net.minecraft.item.Items
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.math.BlockPos

object BiomeExtractor : SpiritExtractor<BiomeSpirit> {
    override fun getColor(): Int = DyeColor.GREEN.signColor

    override fun getExtractedName(): Text = Text.translatable("hexlink.extractor.biome")

    override fun canExtract(target: Entity): Boolean {
        val stack=ExtractorHelper.stack(target)
        return stack!=null && stack.item==Items.DIAMOND
    }

    override fun extract(target: Entity): SpiritExtractor.ExtractionResult<BiomeSpirit> {
        val stack=ExtractorHelper.stackOrThrow(target)
        val biome=target.world.getBiome(BlockPos(target.pos))
        return SpiritExtractor.ExtractionResult(BiomeSpirit(biome),stack.count)
    }

    override fun consume(target: Entity) {
        ExtractorHelper.killStack(target)
    }

}