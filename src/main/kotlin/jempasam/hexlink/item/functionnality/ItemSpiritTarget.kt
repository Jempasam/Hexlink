package jempasam.hexlink.item.functionnality

import jempasam.hexlink.spirit.inout.SpiritTarget
import net.minecraft.item.ItemStack

interface ItemSpiritTarget{
    fun getSpiritTarget(stack: ItemStack): SpiritTarget
}