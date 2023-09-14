package jempasam.hexlink.entity.block

import jempasam.hexlink.block.SpiritContainerBlock
import jempasam.hexlink.block.functionnality.BlockSpiritContainer
import jempasam.hexlink.entity.HexlinkEntities
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.inout.SpiritSource
import jempasam.hexlink.spirit.inout.SpiritTarget
import jempasam.hexlink.utils.NbtHelper
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.network.Packet
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import kotlin.random.Random


class BigTabletBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(HexlinkEntities.BIG_TABLET, pos, state), SpiritTarget, SpiritSource, BlockSpiritContainer{

    fun sendToClient(){
        world?.updateListeners(pos,world?.getBlockState(pos),world?.getBlockState(pos), Block.NOTIFY_LISTENERS)
    }


    fun tick(world: World, pos: BlockPos, state: BlockState) {
        if(world.isClient){
            if(content.size>0 && random.nextInt(20)==0){
                val i=random.nextInt(content.size)
                val spirit= content[i]
                SpiritContainerBlock.coloredParticle(world,pos,spirit.getColor(),1)
            }
        }
    }

    override fun extract(count: Int, spirit: Spirit): SpiritSource.SpiritOutputFlux {
        var currentCount=0
        val removedContent= mutableListOf<Int>()
        for(i in (content.size-1) downTo 0){
            val spi=content[i]
            if(spi==spirit){
                currentCount++
                removedContent.add(i)
                if(currentCount>=count)break
            }
        }
        return SpiritSource.SpiritOutputFlux({
            var i=0
            for(id in removedContent){
                i++
                if(i>it)break
                content.removeAt(id)
            }
            markDirty()
            sendToClient()
        }, currentCount)
    }

    override fun last(): Spirit? {
        if(content.isNotEmpty())return content.last()
        return null
    }

    override fun fill(count: Int, spirit: Spirit): SpiritTarget.SpiritInputFlux {
        return SpiritTarget.SpiritInputFlux({
            for(i in 0..<count)content.add(spirit)
            markDirty()
            sendToClient()
        }, count)
    }

    override fun getSpiritContent(slot: Int, world: World, pos: BlockPos): Sequence<Spirit> {
            return content.asSequence()
    }

    override fun getSlotCount(): Int = 1

    override fun writeNbt(nbt: NbtCompound) {
        val contentNbt=NbtList()
        for(spirit in content)contentNbt.add(NbtHelper.writeSpirit(spirit))
        nbt.put("content", contentNbt)
    }

    override fun readNbt(nbt: NbtCompound) {
        nbt.getList("content",NbtElement.COMPOUND_TYPE.toInt()).forEach{
            if(it is NbtCompound)NbtHelper.readSpirit(it)?.also{content.add(it)}
        }
    }

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener> {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    override fun toInitialChunkDataNbt(): NbtCompound {
        return createNbt()
    }

    val content= mutableListOf<Spirit>()

    val random=Random(System.currentTimeMillis())
}

