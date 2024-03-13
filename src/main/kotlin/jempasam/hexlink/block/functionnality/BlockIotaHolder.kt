package jempasam.hexlink.block.functionnality

import at.petrak.hexcasting.api.casting.iota.Iota
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

interface BlockIotaHolder {
    fun getIota(blockpos: BlockPos, world: ServerWorld): Iota
}