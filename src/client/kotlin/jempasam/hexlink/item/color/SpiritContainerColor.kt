package jempasam.hexlink.item.color

import jempasam.hexlink.item.SpiritContainerItem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.color.item.ItemColorProvider
import net.minecraft.item.ItemStack

class SpiritContainerColor : ItemColorProvider {
    override fun getColor(stack: ItemStack, tintIndex: Int): Int {
        val item=stack.item
        if(item is SpiritContainerItem){
            if(tintIndex==0)return -1
            else if(tintIndex==1)return item.getExtractor(stack)?.getColor() ?: -1
            else if(tintIndex==2 && MinecraftClient.getInstance().world!=null){
                val iota=item.getSpiritIota(stack)
                if(iota!=null)return iota.getSpirit().getColor()
                else return -1
            }
        }
        return -1
    }
}