package jempasam.hexlink.item

import at.petrak.hexcasting.common.items.ItemStaff
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.utils.NbtAdapterList
import jempasam.hexlink.utils.NbtHelper
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.text.Text
import net.minecraft.world.World

class SpiritualStaffItem(settings: Settings): ItemStaff(settings) {

    fun getSpirits(stack: ItemStack): NbtAdapterList<Spirit>
        = SpiritList(stack.nbt?.getList("spirits",NbtElement.COMPOUND_TYPE.toInt()) ?: NbtList())

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        for(spirit in getSpirits(stack))tooltip.add(spirit.getName())
    }

    class SpiritList(nbt: NbtList): NbtAdapterList<Spirit>(nbt){
        override fun from(e: NbtElement): Spirit? = if(e is NbtCompound) NbtHelper.readSpirit(e) else null
        override fun to(e: Spirit): NbtElement = NbtHelper.writeSpirit(e)
    }
}