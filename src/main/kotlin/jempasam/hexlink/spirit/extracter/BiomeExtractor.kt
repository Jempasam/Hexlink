package jempasam.hexlink.spirit.extracter

import jempasam.hexlink.spirit.BiomeSpirit
import jempasam.hexlink.spirit.StackHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.math.BlockPos

object BiomeExtractor : SpiritExtractor<BiomeSpirit> {
    override fun getColor(): Int = DyeColor.GREEN.signColor

    override fun getName(): Text = Text.translatable("hexlink.extractor.biome")

    override fun extract(caster: PlayerEntity?, target: Entity): SpiritExtractor.ExtractionResult<BiomeSpirit> {
        val worldStack=StackHelper.stack(caster,target) ?: return noResult()
        val stack= worldStack.stack
        if(stack.item==Items.DIAMOND){
            val biome=target.world.getBiome(BlockPos(target.pos))
            return SpiritExtractor.ExtractionResult(
                    { worldStack.killer() },
                    BiomeSpirit(biome),
                    stack.count
            )
        }
        else return noResult()
    }

}