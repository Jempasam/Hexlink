package jempasam.hexlink.block

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import jempasam.hexlink.block.functionnality.BlockIotaHolder
import jempasam.hexlink.entity.functionnality.IotaHolder
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World

class IotaHolderBlock<T: BlockEntity>(settings: Settings, private val type: ()->BlockEntityType<T>, private val ticker: BlockEntityTicker<T>, private val shape: VoxelShape)
    : BlockWithEntity(settings), BlockIotaHolder{

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? = type().instantiate(pos,state)

    @Deprecated("Call AbstractBlockState.getRenderType")
    override fun getRenderType(state: BlockState): BlockRenderType = BlockRenderType.MODEL


    override fun <T : BlockEntity?> getTicker(world: World?, state: BlockState?, type: BlockEntityType<T>?): BlockEntityTicker<T>? {
        return checkType(type, this.type()){w,p,s,be -> ticker.tick(w,p,s,be)}
    }

    override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos, newState: BlockState, moved: Boolean) {
        if (!state.isOf(newState.block) && world is ServerWorld) {
            val center=Vec3d.ofCenter(pos)
            world.spawnParticles(
                    ParticleTypes.CLOUD,
                    center.x, center.y, center.z,
                    10,
                    0.5, 0.5, 0.5,
                    0.1
            )
        }

        super.onStateReplaced(state, world, pos, newState, moved)
    }

    override fun getIota(blockpos: BlockPos, world: ServerWorld): Iota
        = (world.getBlockEntity(blockpos) as? IotaHolder)?.getIota() ?: NullIota()

    override fun getOutlineShape(state: BlockState?, world: BlockView?, pos: BlockPos?, context: ShapeContext?): VoxelShape {
        return shape;
    }
}