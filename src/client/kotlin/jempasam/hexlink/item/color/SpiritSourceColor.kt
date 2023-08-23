package jempasam.hexlink.item.color

import jempasam.hexlink.item.functionnality.ItemSpiritSource
import net.minecraft.client.color.item.ItemColorProvider
import net.minecraft.item.ItemStack
import net.minecraft.util.DyeColor

class SpiritSourceColor : ItemColorProvider {
    override fun getColor(stack: ItemStack, tintIndex: Int): Int {
        val item=stack.item
        if(item is ItemSpiritSource){
            if(tintIndex==0)return -1
            else if(tintIndex==1)return item.getSpiritSource(stack).last()?.getColor() ?: DyeColor.GRAY.signColor
        }
        return -1
    }
}