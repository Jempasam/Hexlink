package jempasam.hexlink.block

import jempasam.hexlink.block.functionnality.BlockSpiritContainer
import jempasam.hexlink.block.functionnality.BlockSpiritSource
import jempasam.hexlink.block.functionnality.BlockSpiritTarget
import jempasam.hexlink.entity.block.HexVortexBlockEntity
import jempasam.hexlink.particle.HexlinkParticles
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.inout.SpiritSource
import jempasam.hexlink.spirit.inout.SpiritTarget
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

open class SpiritContainerBlock<T: BlockEntity>(settings: Settings, private val type: ()->BlockEntityType<T>, private val ticker: BlockEntityTicker<T>, private val slot_count: Int, private val shape: VoxelShape)
    : BlockWithEntity(settings), BlockSpiritSource, BlockSpiritTarget, BlockSpiritContainer{

    companion object{
        fun coloredParticle(world: World, pos: BlockPos, color: Int, count: Int){
            val center=Vec3d.of(pos)
            val r = (color shr 16 and 0xFF).toDouble() / 255.0
            val g = (color shr 8 and 0xFF).toDouble() / 255.0
            val b = (color shr 0 and 0xFF).toDouble() / 255.0
            for (j in 0 until count) {
                val pos=center.add(Math.random(), Math.random(),Math.random())
                world.addParticle(
                    HexlinkParticles.SPIRIT,
                    pos.x, pos.y, pos.z,
                    r, g, b
                )
            }
        }
    }

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

    override fun getSpiritSource(world: ServerWorld, pos: BlockPos): SpiritSource {
        return world.getBlockEntity(pos) as? SpiritSource ?: SpiritSource.NONE
    }

    override fun getSpiritTarget(world: ServerWorld, pos: BlockPos): SpiritTarget {
        return world.getBlockEntity(pos) as? SpiritTarget ?: SpiritTarget.NONE
    }

    fun addAt(world: ServerWorld, pos: BlockPos): Boolean{
        val bstate=world.getBlockState(pos)
        val block=bstate.block
        if(bstate.isAir){
            world.setBlockState(pos,defaultState)
        }
        else if(block!=this)return false
        val vortexEntity=world.getBlockEntity(pos)
        vortexEntity as HexVortexBlockEntity
        return true
    }

    fun canAddAt(world: ServerWorld, pos: BlockPos): Boolean{
        val bstate=world.getBlockState(pos)
        val block=bstate.block
        return bstate.isAir || block==this
    }

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        return shape;
    }

    override fun getSpiritContent(slot: Int, world: World, pos: BlockPos): Sequence<Spirit>
        = (world.getBlockEntity(pos) as? BlockSpiritContainer)?.getSpiritContent(slot,world,pos)
                ?: listOf<Spirit>().asSequence()

    override fun getSlotCount(): Int = slot_count
}