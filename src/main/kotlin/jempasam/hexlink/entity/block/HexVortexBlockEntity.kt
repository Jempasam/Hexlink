package jempasam.hexlink.entity.block

import at.petrak.hexcasting.api.utils.serializeToNBT
import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.utils.RecipeHelper
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.recipe.RecipeType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class HexVortexBlockEntity(type: BlockEntityType<out HexVortexBlockEntity>, pos: BlockPos, state: BlockState) : BlockEntity(type, pos, state){

    companion object{
        const val LOADING_TIME=20
    }

    fun tick(world: World, pos: BlockPos, state: BlockState) {
        loading++
        if(loading> LOADING_TIME){
            HexlinkMod.logger.info("TICK VORTEX")
            val inputs=input.toMutableList()
            inputs.addAll(output)
            val result= RecipeHelper.craft(world, RecipeType.CRAFTING, inputs)
            if(result!=null){
                var toremove=result.second
                while(toremove>0 && input.size>0){
                    input.removeAt(0)
                    toremove--
                }
                while(toremove>0 && output.size>0){
                    output.removeAt(0)
                    toremove--
                }
                for(i in 0 until result.first.count){
                    val out=result.first.copy()
                    out.count=1
                    output.add(out)
                }
            }
            loading=0
            if(input.isEmpty()){
                world.removeBlock(pos,false)
            }
        }
        markDirty()
    }

    fun give(stack: ItemStack){
        for(i in 0 until stack.count){
            val out=stack.copy()
            out.count=1
            output.add(out)
        }
        loading=0
        markDirty()
    }

    override fun writeNbt(nbt: NbtCompound) {
        nbt.putInt("loading", loading)
        val input_nbt=NbtList()
        val output_nbt=NbtList()
        for(item in input)input_nbt.add(item.serializeToNBT())
        for(item in output)output_nbt.add(item.serializeToNBT())
        nbt.put("input",input_nbt)
        nbt.put("output",output_nbt)
    }

    override fun readNbt(nbt: NbtCompound) {
        loading=nbt.getInt("loading")
        nbt.getList("input",NbtElement.COMPOUND_TYPE.toInt()).forEach{
            if(it is NbtCompound)input.add(ItemStack.fromNbt(it))
        }
        nbt.getList("output",NbtElement.COMPOUND_TYPE.toInt()).forEach{
            if(it is NbtCompound)output.add(ItemStack.fromNbt(it))
        }
    }

    private var loading=0
    private val input= mutableListOf<ItemStack>()
    private val output= mutableListOf<ItemStack>()
}