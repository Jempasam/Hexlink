package jempasam.hexlink.entity.block

import jempasam.hexlink.block.SpiritContainerBlock
import jempasam.hexlink.block.functionnality.BlockSpiritContainer
import jempasam.hexlink.entity.HexlinkEntities
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.bag.SpiritBag
import jempasam.hexlink.spirit.inout.SpiritSource
import jempasam.hexlink.spirit.inout.SpiritTarget
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.Packet
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import kotlin.math.min
import kotlin.random.Random


class BigTabletBlockEntity(pos: BlockPos, state: BlockState, val size: Int) : BlockEntity(HexlinkEntities.BIG_TABLET, pos, state), SpiritTarget, SpiritSource, BlockSpiritContainer{

    fun sendToClient(){
        world?.updateListeners(pos,world?.getBlockState(pos) ,world?.getBlockState(pos), Block.NOTIFY_LISTENERS)
    }


    fun tick(world: World, pos: BlockPos, state: BlockState) {
        if(world.isClient){
            if(content.size>0 && world.time.toInt()%8==0){
                SpiritContainerBlock.coloredParticle(world,pos,content.random().getColor(),1)
            }
        }
    }


    /* FLUX */
    override fun extract(count: Int, spirit: Spirit): SpiritSource.SpiritOutputFlux {
        val currentCount=Math.min(count,content.count(spirit))
        return SpiritSource.SpiritOutputFlux(currentCount) {
            content.remove(spirit,it)
            markDirty()
            sendToClient()
        }
    }

    override fun last(): Spirit? {
        return if(content.isNotEmpty()) content.last() else null
    }

    override fun all(): Set<Spirit> {
        return content.toSet()
    }

    override fun fill(count: Int, spirit: Spirit): SpiritTarget.SpiritInputFlux {
        val currentCount= min(count, size-content.size)
        return SpiritTarget.SpiritInputFlux({
            content.pushBack(spirit, it)
            markDirty()
            sendToClient()
        }, currentCount)
    }


    /* CONTENT */
    override fun getSpiritContent(slot: Int, world: World, pos: BlockPos): Sequence<Spirit>
        = content.asSequence()

    override fun getSlotCount(): Int = 1


    /* NBT */
    override fun writeNbt(nbt: NbtCompound) {
        nbt.put("content", content.writeNBT())
    }

    override fun readNbt(nbt: NbtCompound) {
        content.readNBT(nbt.getList("content",10))
        world?.updateListeners(pos,world?.getBlockState(pos) ,world?.getBlockState(pos), Block.NOTIFY_LISTENERS)
    }

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener>
        = BlockEntityUpdateS2CPacket.create(this)

    override fun toInitialChunkDataNbt(): NbtCompound = createNbt()

    val content= SpiritBag()

    val random= Random(System.currentTimeMillis())
}

