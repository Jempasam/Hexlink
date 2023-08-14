package jempasam.hexlink.entity.block

import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.utils.RecipeHelper
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.ItemStack
import net.minecraft.recipe.RecipeType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class HexVortexBlockEntity(type: BlockEntityType<out HexVortexBlockEntity>, pos: BlockPos, state: BlockState) : BlockEntity(type, pos, state){

    companion object{

        fun tick(world: World, pos: BlockPos, state: BlockState, be: HexVortexBlockEntity) {
            be.loading++
            if(be.loading> LOADING_TIME){
                HexlinkMod.logger.info("TICK VORTEX")
                val inputs=be.input.toMutableList()
                inputs.addAll(be.output)
                val result=RecipeHelper.craft(world, RecipeType.CRAFTING, inputs)
                if(result!=null){
                    var toremove=result.second
                    while(toremove>0 && be.input.size>0){
                        be.input.removeAt(0)
                        toremove--
                    }
                    while(toremove>0 && be.output.size>0){
                        be.output.removeAt(0)
                        toremove--
                    }
                    for(i in 0 until result.first.count){
                        val out=result.first.copy()
                        out.count=1
                        be.output.add(out)
                    }
                }
                be.loading=0
                if(be.input.isEmpty()){
                    world.removeBlock(pos,false)
                }
            }
        }

        const val LOADING_TIME=20
    }

    private var loading=0
    private val input= mutableListOf<ItemStack>()
    private val output=mutableListOf<ItemStack>()
}