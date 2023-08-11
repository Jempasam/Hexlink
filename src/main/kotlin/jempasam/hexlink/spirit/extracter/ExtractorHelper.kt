package jempasam.hexlink.spirit.extracter

import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.decoration.ItemFrameEntity
import net.minecraft.item.ItemStack

object ExtractorHelper {
    fun stack(target: Entity): ItemStack?{
        return when(target){
            is ItemEntity -> target.stack
            is ItemFrameEntity -> target.heldItemStack
            else -> null
        }
    }

    fun killStack(target: Entity){
        when(target){
            is ItemEntity -> target.kill()
            is ItemFrameEntity -> target.heldItemStack=ItemStack.EMPTY
            else -> throw Error("Assume nullity has been checked but not")
        }
    }

    fun stackOrThrow(target: Entity): ItemStack{
        return when(target){
            is ItemEntity -> target.stack
            is ItemFrameEntity -> target.heldItemStack
            else -> throw Error("Assume nullity has been checked but not")
        }
    }
}