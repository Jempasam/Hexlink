package jempasam.hexlink.item.functionnality

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.utils.asCompound
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.world.World

interface SpellHolderItem {

    fun getSpellNbt(stack: ItemStack): NbtList?

    fun setSpellNbt(stack: ItemStack, list: NbtList)

    fun hasSpell(stack: ItemStack): Boolean
        = getSpellNbt(stack)!=null

    fun getSpell(stack: ItemStack, level: ServerWorld): List<Iota>
        = getSpellNbt(stack)
            ?.map { IotaType.deserialize(it.asCompound,level) }
            ?: listOf()

    fun setSpell(stack: ItemStack, spell: List<Iota>){
        val list=NbtList()
        spell.forEach { list.add(IotaType.serialize(it)) }
        setSpellNbt(stack,list)
    }

    fun appendSpellTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        getSpellNbt(stack)?.let { tooltip.add(ListIota.TYPE.display(it as NbtElement)) }
    }
}