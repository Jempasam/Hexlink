package jempasam.hexlink.block.functionnality

import jempasam.hexlink.spirit.inout.SpiritTarget
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

interface BlockSpiritTarget {
    fun getSpiritTarget(world: ServerWorld, pos: BlockPos): SpiritTarget
}