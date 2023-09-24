package jempasam.hexlink.item.color

import at.petrak.hexcasting.common.items.ItemSpellbook
import jempasam.hexlink.item.UpgradedBookItem
import net.minecraft.client.color.item.ItemColorProvider
import net.minecraft.item.ItemStack

object SpellBookColor : ItemColorProvider {
    override fun getColor(stack: ItemStack, tintIndex: Int): Int {
        val item=stack.item as UpgradedBookItem
        val selected=ItemSpellbook.getPage(stack,1);
        val colors=item.colors(stack);
        if(colors.isEmpty()){
            if(tintIndex==1)return 0xFF0000
            else if(tintIndex==2)return 0xFFBD24
        }
        else{
            if(tintIndex==1)return colors.get(0)?.takeIf { it!=-1 } ?: 0xFF0000
            else if(tintIndex==2)return colors.takeIf { selected<=it.size } ?.get(selected-1) ?.takeIf { it!=-1 } ?: 0xFFBD24
        }
        if(tintIndex==3)return item.getIotaColor(stack)
        return -1
    }
}