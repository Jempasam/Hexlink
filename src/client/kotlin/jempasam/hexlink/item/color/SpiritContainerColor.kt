package jempasam.hexlink.item.color

import jempasam.hexlink.item.SoulContainerItem
import net.minecraft.client.color.item.ItemColorProvider
import net.minecraft.item.ItemStack

class SpiritContainerColor : ItemColorProvider {
    override fun getColor(stack: ItemStack, tintIndex: Int): Int {
        val item=stack.item
        if(item is SoulContainerItem){
            if(tintIndex==0)return -1
            else if(tintIndex==1)return item.getItemBarColor(stack)
        }
        return -1
    }
}