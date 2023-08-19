package jempasam.hexlink.entity.block

import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.HexlinkRegistry
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
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World


class HexVortexBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(HexlinkEntities.HEX_VORTEX, pos, state){

    companion object{
        const val LOADING_TIME=20
    }

    fun craft(): Boolean{
        val inputs=input.toMutableList()
        inputs.addAll(output)
        for(handler in HexlinkRegistry.HEXVORTEX_HANDLER){
            val recipe=handler.findRecipe(inputs)
            if(recipe!=null){
                val final_inputs=inputs.subList(0,recipe.ingredientCount())
                if(recipe.test(final_inputs)){
                    for(i in 0..<recipe.ingredientCount()){
                        if(input.size>0)input.removeAt(0)
                        else if(output.size>0)output.removeAt(0)
                        else break
                    }
                    val result=recipe.mix(final_inputs)
                    output.addAll(result)
                    markDirty()
                    sendToClient()
                    return true
                }
            }
        }
        return false
    }

    private fun sendToClient(){
        world?.updateListeners(pos,world?.getBlockState(pos),world?.getBlockState(pos), Block.NOTIFY_LISTENERS)
    }

    fun tick(world: World, pos: BlockPos, state: BlockState) {
        if(world.isClient)return
        loading++
        if(loading> LOADING_TIME){
            HexlinkMod.logger.info("TICK VORTEX")
            craft()
            loading=0
            if(input.isEmpty() && output.isEmpty()){
                world.removeBlock(pos,false)
            }
        }
    }

    fun give(spirit: Spirit){
        input.add(spirit)
        loading=0
        markDirty()
        sendToClient()
    }

    override fun writeNbt(nbt: NbtCompound) {
        nbt.putInt("loading", loading)
        val input_nbt=NbtList()
        val output_nbt=NbtList()
        for(spirit in input)input_nbt.add(NbtHelper.writeSpirit(spirit))
        for(spirit in output)output_nbt.add(NbtHelper.writeSpirit(spirit))
        nbt.put("input",input_nbt)
        nbt.put("output",output_nbt)
    }

    override fun readNbt(nbt: NbtCompound) {
        loading=nbt.getInt("loading")
        input.clear()
        nbt.getList("input",NbtElement.COMPOUND_TYPE.toInt()).forEach{
            if(it is NbtCompound)NbtHelper.readSpirit(it)?.also{input.add(it)}
        }
        output.clear()
        nbt.getList("output",NbtElement.COMPOUND_TYPE.toInt()).forEach{
            if(it is NbtCompound)NbtHelper.readSpirit(it)?.also{output.add(it)}
        }
    }

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener> {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    override fun toInitialChunkDataNbt(): NbtCompound {
        return createNbt()
    }

    private var loading=0
    val input= mutableListOf<Spirit>()
    val output= mutableListOf<Spirit>()
}

