package jempasam.hexlink.item.color

import jempasam.hexlink.item.MixedPigmentItem
import net.minecraft.client.color.item.ItemColorProvider
import net.minecraft.item.ItemStack

class MixedPigmentColor : ItemColorProvider {
    override fun getColor(stack: ItemStack, tintIndex: Int): Int {
        val item=stack.item
        if(item is MixedPigmentItem){
            if(tintIndex==0)return item.getColor1(stack)
            else if(tintIndex==1)return item.getColor2(stack)
        }
        return -1
    }
}