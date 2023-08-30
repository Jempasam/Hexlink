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

object StackHelper {


    class WorldStack(val stack: ItemStack, val killer: ()->Unit, val update: ()->Unit, val replace: (ItemStack)->Unit)

    // Stack from entity
    fun stack(caster: PlayerEntity, predicate: (ItemStack)->Boolean): WorldStack?{
        val inventory=caster.inventory
        if(predicate(caster.mainHandStack)) return WorldStack(
                caster.mainHandStack,
                {caster.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY)},
                {caster.setStackInHand(Hand.MAIN_HAND,caster.mainHandStack)},
                {caster.setStackInHand(Hand.MAIN_HAND,it)}
        )

        if(predicate(caster.offHandStack)) return WorldStack(
                caster.offHandStack,
                {caster.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY)},
                {caster.setStackInHand(Hand.OFF_HAND,caster.offHandStack)},
                {caster.setStackInHand(Hand.OFF_HAND,it)}
        )

        for(i in 0 ..< inventory.size()){
            val stack= inventory.getStack(i)
            if(predicate(stack))return WorldStack(
                    stack,
                    {inventory.setStack(i, ItemStack.EMPTY)},
                    {inventory.setStack(i, inventory.getStack(i))},
                    {inventory.setStack(i, it)}
            )
        }
        return null
    }

    fun stack(caster: PlayerEntity?, target: Entity): WorldStack?{
        return when(target){
            is ItemEntity
                -> WorldStack(target.stack, {target.kill()}, {target.stack=target.stack}, {target.stack=it})
            is ItemFrameEntity
                -> WorldStack(target.heldItemStack, {target.heldItemStack=ItemStack.EMPTY}, {target.heldItemStack=target.heldItemStack}, {target.heldItemStack=it})
            is PlayerEntity
                -> WorldStack(target.offHandStack, {target.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY)}, {target.setStackInHand(Hand.OFF_HAND,target.offHandStack)}, {target.setStackInHand(Hand.OFF_HAND, it)})
            else -> null
        }
    }

    fun stackOrThrow(caster: PlayerEntity?, target: Entity): WorldStack
        = stack(caster,target) ?: throw Error("Checked not null, but finally is null")



    // Stack from block pos
    fun stack(caster: PlayerEntity?, world: ServerWorld, pos: Vec3d): WorldStack?{
        val bpos=BlockPos(pos)
        val blockInv=HopperBlockEntity.getInventoryAt(world,bpos)
        if(blockInv!=null){
            for (i in 0 until blockInv.size()) {
                if (blockInv.getStack(i).isEmpty()) continue
                val stack = blockInv.getStack(i).copy()
                if (stack != null) return WorldStack(stack, {blockInv.setStack(i, ItemStack.EMPTY)}, {blockInv.setStack(i, blockInv.getStack(i))}, {blockInv.setStack(i,it)})
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