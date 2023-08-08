package jempasam.hexlink.utils

import jempasam.hexlink.HexlinkRegistry
import jempasam.hexlink.spirit.Spirit
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier

object NbtHelper {
    fun readSpirit(nbt: NbtCompound): Spirit?{
        val type_id=nbt.getString("type")
        if(type_id.isEmpty())return null
        val type= HexlinkRegistry.SPIRIT.get(Identifier(type_id))
        if(type==null)return null

        val value_nbt=nbt.get("value")
        if(value_nbt==null)return null
        return type.deserialize(value_nbt)
    }

    fun writeSpirit(spirit: Spirit): NbtCompound{
        val compound=NbtCompound()
        compound.putString("type", HexlinkRegistry.SPIRIT.getId(spirit.getType()).toString())
        compound.put("value", spirit.serialize())
        return compound
    }
}