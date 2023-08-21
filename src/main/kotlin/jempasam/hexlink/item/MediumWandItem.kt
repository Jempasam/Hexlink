package jempasam.hexlink.item

import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.utils.asCompound
import at.petrak.hexcasting.api.utils.asList
import at.petrak.hexcasting.api.utils.getInt
import at.petrak.hexcasting.api.utils.getOrCreateList
import at.petrak.hexcasting.common.items.magic.ItemArtifact
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.server.world.ServerWorld

class MediumWandItem(settings: Settings, maximum_spell: Int) : ItemArtifact(settings){
    
    companion object{
        const val TAG_SELECTED: String="selectedPattern"
    }

    fun getSelectedSpellNbt(stack: ItemStack): NbtList?{
        val selectedspell=stack.getInt(TAG_SELECTED, 0)
        val patsTag = stack.getOrCreateList(TAG_PROGRAM, NbtElement.LIST_TYPE.toInt())
        if(selectedspell<0 || patsTag.size <= 0)return null

        val actual_spell= patsTag[selectedspell % patsTag.size]
        if(actual_spell.type != NbtElement.LIST_TYPE)return null
        if(actual_spell.asList.heldType != NbtElement.COMPOUND_TYPE)return null

        return actual_spell.asList
    }

    fun getSpellCount(stack: ItemStack): Int{
        val patsTag = stack.getOrCreateList(TAG_PROGRAM, NbtElement.LIST_TYPE.toInt())
        return patsTag.size
    }

    fun addSpell(stack: ItemStack){
        val patsTag = stack.getOrCreateList(TAG_PROGRAM, NbtElement.LIST_TYPE.toInt())
        patsTag.add(NbtList())
    }

    override fun getHex(stack: ItemStack, level: ServerWorld): List<Iota> {
        val actual_spell= getSelectedSpellNbt(stack) ?: return emptyList()

        val out = ArrayList<Iota>()
        for (patTag in actual_spell.asList) {
            val tag=patTag.asCompound
            out.add(HexIotaTypes.deserialize(tag, level))
        }
        return out
    }

    override fun writeHex(stack: ItemStack, program: List<Iota>, media: Int) {
        val actual_spell= getSelectedSpellNbt(stack) ?: return
        val selected_num=stack.getInt(TAG_SELECTED, 0)
        val spell_count=getSpellCount(stack)
        if(selected_num==spell_count-1)addSpell(stack)
        
        actual_spell.clear()
        for (pat in program) {
            actual_spell.add(HexIotaTypes.serialize(pat))
        }

        withMedia(stack, media, media)
    }
}