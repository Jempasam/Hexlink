package jempasam.hexlink.item.color

import jempasam.hexlink.item.functionnality.ItemSpiritSource
import net.minecraft.client.color.item.ItemColorProvider
import net.minecraft.item.ItemStack
import net.minecraft.util.DyeColor

class SpiritSourceColor(val index: Int=1) : ItemColorProvider {
    override fun getColor(stack: ItemStack, tintIndex: Int): Int {
        val item=stack.item
        if(item is ItemSpiritSource){
            if(tintIndex==index)return item.getSpiritSource(stack).last()?.getColor() ?: DyeColor.GRAY.signColor
        }
        return -1
    }
}