package jempasam.hexlink.block.color

import jempasam.hexlink.spirit.inout.SpiritSource
import net.minecraft.block.BlockState
import net.minecraft.client.color.block.BlockColorProvider
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockRenderView

class SpiritContainerBlockColor : BlockColorProvider {
    override fun getColor(state: BlockState, world: BlockRenderView?, pos: BlockPos?, tintIndex: Int): Int {
        if(pos!=null && world!=null){
            val entity=world.getBlockEntity(pos)
            if(entity is SpiritSource){
                val last=entity.last()
                if(last!=null)return last.getColor()
            }
        }
        return 0xffffff
    }
}