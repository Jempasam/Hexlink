package jempasam.hexlink.block

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView

class FacingSpiritContainerBlock<T: BlockEntity>(
    settings: Settings,
    type: () -> BlockEntityType<T>,
    ticker: BlockEntityTicker<T>,
    slot_count: Int, shape: VoxelShape
) : SpiritContainerBlock<T>(settings, type, ticker, slot_count, shape){

    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState {
        return state.with(FACING, rotation.rotate(state.get(FACING) as Direction))
    }

    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState {
        return state.rotate(mirror.getRotation(state.get(FACING) as Direction))
    }

    override fun appendProperties(builder: StateManager.Builder<Block?, BlockState>) {
        super.appendProperties(builder)
        builder.add(FACING)
    }

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        val base=super.getOutlineShape(state, world, pos, context)
        if(state.get(FACING).axis==Direction.Axis.Z){
            return base
        }
        else{
            return base.boundingBoxes
                .map { Box(it.minZ,it.minY,it.minX, it.maxZ,it.maxY,it.maxX) }
                .map { VoxelShapes.cuboid(it) }
                .reduceRight{a,b -> VoxelShapes.union(a,b)}
        }
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        for (direction in ctx.placementDirections) {
            if (direction.axis.isHorizontal) {
                return defaultState.with(FACING, direction.opposite)
            }
        }
        return defaultState
    }

    companion object{
        val FACING=HorizontalFacingBlock.FACING
    }
}