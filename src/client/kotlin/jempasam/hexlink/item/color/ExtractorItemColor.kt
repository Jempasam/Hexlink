package jempasam.hexlink.item.color

import jempasam.hexlink.item.functionnality.ExtractorItem
import net.minecraft.client.color.item.ItemColorProvider
import net.minecraft.item.ItemStack

class ExtractorItemColor : ItemColorProvider {
    override fun getColor(stack: ItemStack, tintIndex: Int): Int {
        val item=stack.item
        if(item is ExtractorItem){
            if(tintIndex==0)return -1
            else if(tintIndex==1)return item.getExtractor(stack)?.getColor() ?: -1
        }
        return -1
    }
}