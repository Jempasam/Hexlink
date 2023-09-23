package jempasam.hexlink.item.color

import net.minecraft.client.color.item.ItemColorProvider
import net.minecraft.item.DyeableItem
import net.minecraft.item.ItemStack

class DyeableColor(val index: Int=1) : ItemColorProvider {
    override fun getColor(stack: ItemStack, tintIndex: Int): Int {
        val item=stack.item
        if(item is DyeableItem){
            if(tintIndex==index)return item.getColor(stack)
        }
        return -1
    }
}