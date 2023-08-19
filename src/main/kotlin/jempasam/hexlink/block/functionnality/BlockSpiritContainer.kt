package jempasam.hexlink.block.functionnality

import jempasam.hexlink.spirit.Spirit
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

interface BlockSpiritContainer {
    fun getSpiritContent(world: World, pos: BlockPos): Sequence<Spirit>
}