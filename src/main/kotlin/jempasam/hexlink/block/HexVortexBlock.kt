package jempasam.hexlink.block

import jempasam.hexlink.entity.block.HexVortexBlockEntity
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class HexVortexBlock(val type: BlockEntityType<out HexVortexBlockEntity>, settings: Settings) : BlockWithEntity(settings){
    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return type.instantiate(pos,state)
    }

    override fun <T : BlockEntity?> getTicker(world: World?, state: BlockState?, type: BlockEntityType<T>?): BlockEntityTicker<T>? {
        return checkType(type, this.type){w,p,s,be -> be.tick(w,p,s)}
    }
}