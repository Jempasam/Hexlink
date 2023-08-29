package jempasam.hexlink.utils

import jempasam.hexlink.HexlinkRegistry
import jempasam.hexlink.spirit.Spirit
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object NbtHelper {
    fun readSpirit(nbt: NbtCompound): Spirit?{
        val typeId=nbt.getString("type")
        if(typeId.isEmpty())return null
        val type= HexlinkRegistry.SPIRIT.get(Identifier(typeId)) ?: return null

        val value_nbt= nbt.get("value") ?: return null
        return type.deserialize(value_nbt)
    }

    fun writeSpirit(spirit: Spirit): NbtCompound{
        val compound=NbtCompound()
        compound.putString("type", HexlinkRegistry.SPIRIT.getId(spirit.getType()).toString())
        compound.put("value", spirit.serialize())
        return compound
    }

    fun <T>readRegistry(reg: Registry<T>, nbt: NbtElement): T?
            = (nbt as? NbtString)
            ?.let { reg.get(Identifier.tryParse(it.asString())) }

    fun <T>writeRegistry(reg: Registry<T>, value: T?): NbtElement
            = value
                    ?.let { reg.getId(value) }
                    ?.let { NbtString.of(it.toString()) }
                    ?: NbtString.of("")
}