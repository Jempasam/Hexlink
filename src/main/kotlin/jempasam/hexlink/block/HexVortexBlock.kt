package jempasam.hexlink.block

import jempasam.hexlink.block.functionnality.BlockSpiritContainer
import jempasam.hexlink.block.functionnality.BlockSpiritSource
import jempasam.hexlink.block.functionnality.BlockSpiritTarget
import jempasam.hexlink.entity.HexlinkEntities
import jempasam.hexlink.entity.block.HexVortexBlockEntity
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.inout.SpiritSource
import jempasam.hexlink.spirit.inout.SpiritTarget
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.stream.Stream
import kotlin.streams.asSequence

class HexVortexBlock(settings: Settings) : BlockWithEntity(settings), BlockSpiritSource, BlockSpiritTarget, BlockSpiritContainer{
    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return HexlinkEntities.HEX_VORTEX.instantiate(pos,state)
    }

    @Deprecated("Call AbstractBlockState.getRenderType")
    override fun getRenderType(state: BlockState): BlockRenderType = BlockRenderType.MODEL


    override fun <T : BlockEntity?> getTicker(world: World?, state: BlockState?, type: BlockEntityType<T>?): BlockEntityTicker<T>? {
        return checkType(type, HexlinkEntities.HEX_VORTEX){w,p,s,be -> be.tick(w,p,s)}
    }

    fun addAt(world: ServerWorld, pos: BlockPos, spirit: Spirit): Boolean{
        val bstate=world.getBlockState(pos)
        val block=bstate.block
        if(bstate.isAir){
            world.setBlockState(pos,defaultState)
        }
        else if(block!=this)return false
        val vortex_entity=world.getBlockEntity(pos)
        vortex_entity as HexVortexBlockEntity
        vortex_entity.give(spirit)
        return true
    }

    fun canAddAt(world: ServerWorld, pos: BlockPos): Boolean{
        val bstate=world.getBlockState(pos)
        val block=bstate.block
        return bstate.isAir || block==this
    }

    override fun getSpiritSource(world: ServerWorld, pos: BlockPos): SpiritSource {
        val vortexentity=world.getBlockEntity(pos)
        if(vortexentity is HexVortexBlockEntity){
            return object: SpiritSource{
                override fun extract(count: Int, spirit: Spirit): SpiritSource.SpiritOutputFlux {
                    var current_count=0
                    val removed_output= mutableListOf<Int>()
                    val removed_input= mutableListOf<Int>()
                    for(i in (vortexentity.output.size-1)..0){
                        val spi=vortexentity.output[i]
                        if(spi==spirit){
                            current_count++
                            removed_output.add(i)
                            if(current_count>=count)break
                        }
                    }
                    if(current_count<count)for(i in (vortexentity.input.size-1)..0){
                        val spi=vortexentity.input[i]
                        if(spi==spirit){
                            current_count++
                            removed_input.add(i)
                            if(current_count>=count)break
                        }
                    }
                    return SpiritSource.SpiritOutputFlux({
                        for(id in removed_input)vortexentity.input.removeAt(id)
                        for(id in removed_output)vortexentity.output.removeAt(id)
                    }, current_count)
                }
            }
        }
        else return SpiritSource.NONE
    }

    override fun getSpiritTarget(world: ServerWorld, pos: BlockPos): SpiritTarget {
        val vortexentity=world.getBlockEntity(pos)
        if(vortexentity is HexVortexBlockEntity){
            return object: SpiritTarget{
                override fun fill(count: Int, spirit: Spirit): SpiritTarget.SpiritInputFlux {
                    return SpiritTarget.SpiritInputFlux({
                        for(i in 0..<count)vortexentity.input.add(spirit)
                    }, count)
                }
            }
        }
        else return SpiritTarget.NONE
    }

    override fun getSpiritContent(world: World, pos: BlockPos): Sequence<Spirit> {
        val vortex=world.getBlockEntity(pos)
        if(vortex is HexVortexBlockEntity) return Stream.concat(vortex.input.stream(),vortex.output.stream()).asSequence()
        else return listOf<Spirit>().stream().asSequence()
    }
}