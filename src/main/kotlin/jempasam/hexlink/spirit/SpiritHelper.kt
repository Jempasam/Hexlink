package jempasam.hexlink.spirit

import net.minecraft.block.entity.HopperBlockEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.decoration.ItemFrameEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

object SpiritHelper {


    class WorldStack(val stack: ItemStack, val killer: ()->Unit, val update: ()->Unit)

    // Stack from entity
    fun stack(caster: PlayerEntity?, target: Entity): WorldStack?{
        return when(target){
            is ItemEntity
                -> WorldStack(target.stack, {target.kill()}, {target.stack=target.stack})
            is ItemFrameEntity
                -> WorldStack(target.heldItemStack, {target.heldItemStack=ItemStack.EMPTY}, {target.heldItemStack=target.heldItemStack})
            is PlayerEntity
                -> {
                if(target===caster) WorldStack(target.offHandStack, {target.setStackInHand(Hand.OFF_HAND,ItemStack.EMPTY)}, {target.setStackInHand(Hand.OFF_HAND,target.offHandStack)})
                else null
            }
            else -> null
        }
    }

    fun stackOrThrow(caster: PlayerEntity?, target: Entity): WorldStack
        = stack(caster,target) ?: throw Error("Checked not null, but finally is null")



    // Stack from block pos
    fun stack(caster: PlayerEntity?, world: ServerWorld, pos: Vec3d): WorldStack?{
        val bpos=BlockPos(pos)
        val block_inv=HopperBlockEntity.getInventoryAt(world,bpos)
        if(block_inv!=null){
            for (i in 0 until block_inv.size()) {
                if (block_inv.getStack(i).isEmpty()) continue
                val stack = block_inv.getStack(i).copy()
                if (stack != null) return WorldStack(stack, {block_inv.setStack(i, ItemStack.EMPTY)}, {block_inv.setStack(i, block_inv.getStack(i))})
            }
        }
        else{
            val entities=world.getOtherEntities(null, Box(bpos))
            for(entity in entities){
                val ret=stack(caster, entity)
                if(ret!=null)return ret
            }
        }
        return null
    }

    fun stackOrThrow(caster: PlayerEntity?, world: ServerWorld, pos: Vec3d): WorldStack
            = stack(caster,world,pos) ?: throw Error("Checked not null, but finally is null")

}