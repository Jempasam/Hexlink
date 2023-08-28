package jempasam.hexlink.entity.block

import jempasam.hexlink.HexlinkRegistry
import jempasam.hexlink.block.HexVortexBlock
import jempasam.hexlink.entity.HexlinkEntities
import jempasam.hexlink.spirit.Spirit
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
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import kotlin.random.Random


class HexVortexBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(HexlinkEntities.HEX_VORTEX, pos, state){


    companion object{
        const val LOADING_TIME=20
        const val MAX_AGE=20*60
        const val START_TIME=60

        const val CRAFT_SUCCEED_STATUS=260401
    }

    private fun craft(world: ServerWorld): Boolean{
        val inputs=input.toMutableList()
        inputs.addAll(output)
        for(handler in HexlinkRegistry.HEXVORTEX_HANDLER){
            val recipe=handler.findRecipe(inputs, world)
            if(recipe!=null){
                val finalInputs=inputs.subList(0,recipe.ingredientCount())
                for(i in 0..<recipe.ingredientCount()){
                    if(input.size>0)input.removeAt(0)
                    else if(output.size>0)output.removeAt(0)
                    else break
                }
                val result=recipe.mix(finalInputs)
                output.addAll(result)
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
            if(input.size+output.size>0 && random.nextInt(20)==0){
                val i=random.nextInt(input.size+output.size)
                val spirit= if(i>=input.size) output[i-input.size] else input[i]
                HexVortexBlock.coloredParticle(world,pos,spirit.getColor(),1)
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

    fun give(spirit: Spirit){
        input.add(spirit)
        loading=0
        age=0
        markDirty()
        sendToClient()
    }

    override fun writeNbt(nbt: NbtCompound) {
        nbt.putInt("loading", loading)
        nbt.putInt("age", age)
        val inputNbt=NbtList()
        val outputNbt=NbtList()
        for(spirit in input)inputNbt.add(NbtHelper.writeSpirit(spirit))
        for(spirit in output)outputNbt.add(NbtHelper.writeSpirit(spirit))
        nbt.put("input",inputNbt)
        nbt.put("output",outputNbt)
    }

    override fun readNbt(nbt: NbtCompound) {
        loading=nbt.getInt("loading")
        age=nbt.getInt("age")
        input.clear()
        nbt.getList("input",NbtElement.COMPOUND_TYPE.toInt()).forEach{
            if(it is NbtCompound)NbtHelper.readSpirit(it)?.also{input.add(it)}
        }
        val previousOutputSize=output.size
        output.clear()
        nbt.getList("output",NbtElement.COMPOUND_TYPE.toInt()).forEach{
            if(it is NbtCompound)NbtHelper.readSpirit(it)?.also{output.add(it)}
        }

        val w=world
        if(w!=null){
            for(i in previousOutputSize..<output.size){
                HexVortexBlock.coloredParticle(w,pos,output[i].getColor(),6)
            }
        }
    }

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener> {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    override fun toInitialChunkDataNbt(): NbtCompound {
        return createNbt()
    }

    var loading=0
    var age=-START_TIME+ LOADING_TIME
    val input= mutableListOf<Spirit>()
    val output= mutableListOf<Spirit>()

    val random=Random(System.currentTimeMillis())
}

