package jempasam.hexlink.entity.block

import jempasam.hexlink.HexlinkRegistry
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
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import kotlin.math.min
import kotlin.random.Random


class HexVortexBlockEntity(pos: BlockPos, state: BlockState, val size: Int) : BlockEntity(HexlinkEntities.HEX_VORTEX, pos, state), SpiritSource, SpiritTarget, BlockSpiritContainer{


    companion object{
        const val LOADING_TIME=20
        const val MAX_AGE=20*60
        const val START_TIME=60

        const val CRAFT_SUCCEED_STATUS=260401
    }

    private fun craft(world: ServerWorld): Boolean{
        val inputs=SpiritBag(input)
        inputs.pushBack(output)

        for(handler in HexlinkRegistry.HEXVORTEX_HANDLER){
            val recipe=handler.findRecipe(inputs, world)
            if(recipe!=null){
                val finalInputs=inputs.subBag(recipe.ingredientCount())

                val inputIng= min(input.size,recipe.ingredientCount())
                input.popFront(inputIng)
                if(inputIng<=recipe.ingredientCount())output.popFront(recipe.ingredientCount()-inputIng)

                val result=recipe.mix(finalInputs)
                result.forEach {
                    if(output.size+input.size<size)output.pushBack(it,1)
                }
                markDirty()
                sendToClient()
                return true
            }
        }
        return false
    }

    fun sendToClient(){
        world?.updateListeners(pos,world?.getBlockState(pos),world?.getBlockState(pos), Block.NOTIFY_LISTENERS)
    }


    fun tick(world: World, pos: BlockPos, state: BlockState) {
        if(world.isClient){
            if(input.size+output.size>0 && age%5==0){
                val bag= if(input.isEmpty()||Random.nextBoolean()) output else input
                if(!bag.isEmpty())SpiritContainerBlock.coloredParticle(world,pos,bag.random().getColor(),1)
            }
        }
        else{
            loading++
            age++
            if(age> MAX_AGE){
                world.removeBlock(pos,false)
            }
            if(loading> LOADING_TIME){
                if(craft(world as ServerWorld)){
                    age=0
                }
                loading=0
                if(input.isEmpty() && output.isEmpty()){
                    world.removeBlock(pos,false)
                }
            }
        }
    }


    /* FLUX */
    override fun extract(count: Int, spirit: Spirit): SpiritSource.SpiritOutputFlux {
        val outputCount=output.count(spirit)
        val currentCount= min(count, outputCount+input.count(spirit))
        return SpiritSource.SpiritOutputFlux(currentCount) {
            age = 0
            output.remove(spirit,min(it,outputCount))
            if(it>outputCount)input.remove(spirit,it-outputCount)
            markDirty()
            sendToClient()
        }
    }

    override fun last(): Spirit? {
        return when {
            output.isNotEmpty() -> output.last()
            input.isNotEmpty() -> input.last()
            else -> null
        }
    }

    override fun fill(count: Int, spirit: Spirit): SpiritTarget.SpiritInputFlux {
        val currentCount=min(count, size-input.size-output.size)
        return SpiritTarget.SpiritInputFlux({
            age=0
            loading=0
            input.pushBack(spirit,it)
            markDirty()
            sendToClient()
        }, currentCount)
    }


    /* CONTENT */
    override fun getSpiritContent(slot: Int, world: World, pos: BlockPos): Sequence<Spirit> {
        return when (slot) {
            0 -> input.asSequence()
            1 -> output.asSequence()
            else -> listOf<Spirit>().asSequence()
        }
    }

    override fun getSlotCount(): Int = 2


    /* NBT */
    override fun writeNbt(nbt: NbtCompound) {
        nbt.apply {
            putInt("loading", loading)
            putInt("age", age)

            put("input",input.writeNBT())
            put("output",output.writeNBT())
        }
    }

    override fun readNbt(nbt: NbtCompound) {
        loading=nbt.getInt("loading")
        age=nbt.getInt("age")

        val previousOutputSize=output.size
        input.readNBT(nbt.getList("input",10))
        output.readNBT(nbt.getList("output",10))

        val w=world
        if(w!=null){
            val it=output.reverseIterator()
            for(i in previousOutputSize..<output.size){
                SpiritContainerBlock.coloredParticle(w,pos,it.next().getColor(),6)
            }
        }
    }

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener> {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    override fun toInitialChunkDataNbt(): NbtCompound = createNbt()


    var loading=0
    var age=-START_TIME+ LOADING_TIME
    val input= SpiritBag()
    val output= SpiritBag()

    val random=Random(System.currentTimeMillis())
}

