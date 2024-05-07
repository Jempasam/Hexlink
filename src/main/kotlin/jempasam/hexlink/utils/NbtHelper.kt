package jempasam.hexlink.utils

import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.Dynamic
import jempasam.hexlink.HexlinkRegistry
import jempasam.hexlink.spirit.Spirit
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.NbtString
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.registry.Registry

object NbtHelper {
    fun readSpirit(nbt: NbtCompound): Spirit?{
        val typeId=nbt.getString("type")
        if(typeId.isEmpty())return null
        val type= HexlinkRegistry.SPIRIT.get(Identifier(typeId)) ?: return null

        val value_nbt= nbt.get("value") ?: return null
        return type.deserialize(value_nbt)
    }

    fun readSpirit(obj: JsonObject): Spirit{
        val typeId=JsonHelper.getString(obj, "type")
            ?: throw JsonParseException("Missing type id")

        val type= HexlinkRegistry.SPIRIT.get(Identifier(typeId))
            ?: throw JsonParseException("Invalid spirit type \"$typeId\"")

        val value_nbt= obj.get("value")
            ?: throw JsonParseException("Missing spirit value")

        return type.deserialize(value_nbt.asNBT())
            ?: throw JsonParseException("Invalid spirit value for \"$typeId\"")
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

    val ELEMENT_CODEC= Codec.PASSTHROUGH.comapFlatMap(
        { dynamic: Dynamic<*> ->
            val nbtElement = dynamic.convert(NbtOps.INSTANCE).value as NbtElement
            DataResult.success(nbtElement)
        },
        { nbt: NbtElement? ->
            Dynamic( NbtOps.INSTANCE, nbt )
        }
    )
}