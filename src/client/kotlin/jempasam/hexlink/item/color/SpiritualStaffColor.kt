package jempasam.hexlink.item.color

import jempasam.hexlink.item.SpiritualStaffItem
import net.minecraft.client.color.item.ItemColorProvider
import net.minecraft.item.ItemStack

class SpiritualStaffColor : ItemColorProvider {
    override fun getColor(stack: ItemStack, tintIndex: Int): Int {
        val item=stack.item as SpiritualStaffItem
        val list=item.getSpirits(stack)
        if(tintIndex>=list.size)return 0xFFFFFF
        return list[tintIndex]?.getColor() ?: 0xFFFFFF
    }

}