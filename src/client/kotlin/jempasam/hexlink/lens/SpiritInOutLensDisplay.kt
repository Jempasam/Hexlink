package jempasam.hexlink.lens

import at.petrak.hexcasting.api.client.ScryingLensOverlayRegistry.OverlayBuilder
import com.mojang.datafixers.util.Pair
import jempasam.hexlink.block.functionnality.BlockSpiritSource
import jempasam.hexlink.block.functionnality.BlockSpiritTarget
import jempasam.hexlink.item.HexlinkItems
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class SpiritInOutLensDisplay : OverlayBuilder {
    override fun addLines(lines: MutableList<Pair<ItemStack, Text>>, state: BlockState, pos: BlockPos, observer: PlayerEntity, world: World, hitFace: Direction) {
        val block=state.block
        if(block is BlockSpiritTarget){
            lines.add(Pair.of(HexlinkItems.SmallBag.defaultStack,Text.translatable("hexlink.spirit_target")))
        }
        if(block is BlockSpiritSource){
            lines.add(Pair.of(HexlinkItems.SmallBag.defaultStack,Text.translatable("hexlink.spirit_source")))
        }
    }
}