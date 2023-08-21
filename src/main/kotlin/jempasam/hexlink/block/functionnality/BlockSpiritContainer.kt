package jempasam.hexlink.block.functionnality

import jempasam.hexlink.spirit.Spirit
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

interface BlockSpiritContainer {
    fun getSpiritContent(slot: Int, world: World, pos: BlockPos): Sequence<Spirit>
    fun getSlotCount(): Int
}