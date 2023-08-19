package jempasam.hexlink.item.functionnality

import jempasam.hexlink.spirit.inout.SpiritSource
import net.minecraft.item.ItemStack

interface ItemSpiritSource {
    fun getSpiritSource(stack: ItemStack): SpiritSource
}