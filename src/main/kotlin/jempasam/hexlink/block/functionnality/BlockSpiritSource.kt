package jempasam.hexlink.block.functionnality

import jempasam.hexlink.spirit.inout.SpiritSource
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

interface BlockSpiritSource {
    fun getSpiritSource(world: ServerWorld, pos: BlockPos): SpiritSource
}