package jempasam.hexlink.data

import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import jempasam.hexlink.HexlinkRegistry
import jempasam.hexlink.spirit.SpecialSpirit
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.utils.NbtHelper
import jempasam.hexlink.utils.asNBT
import net.minecraft.nbt.NbtCompound
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper

object SpecialSpiritDataLoader: JsonEntryDataLoader("special_spirits") {

    override fun apply(id: Identifier, obj: JsonObject) {
        fun getSpiritOpt(obj: JsonObject, name: String): Spirit? {
            return obj.get(name)
                    ?.asNBT()
                    ?.let { if(it is NbtCompound) NbtHelper.readSpirit(it) else null }
        }

        fun cast(spirit: Spirit?, name: String): Spirit{
            return spirit ?: throw JsonParseException("Missing or invalid \"$name\"")
        }
        val name=obj.get("name")
                ?.let { Text.Serializer.fromJson(it) }
                ?: throw JsonParseException("Missing or invalid \"name\"")

        val color=JsonHelper.getInt(obj,"color")

        var manifestAt: Spirit?=null
        var manifestIn: Spirit?=null
        var lookAt: Spirit?=null
        var lookIn: Spirit?=null

        getSpiritOpt(obj,"all")?.also {
            manifestAt=it
            manifestIn=it
            lookAt=it
            lookIn=it
        }

        getSpiritOpt(obj,"manifest")?.also {
            manifestAt=it
            manifestIn=it
        }

        getSpiritOpt(obj,"look")?.also {
            lookAt=it
            lookIn=it
        }

        getSpiritOpt(obj,"at")?.also {
            manifestAt=it
            lookAt=it
        }

        getSpiritOpt(obj,"in")?.also {
            manifestIn=it
            lookIn=it
        }

        getSpiritOpt(obj,"manifestAt")?.also { manifestAt=it }
        getSpiritOpt(obj,"manifestIn")?.also { manifestIn=it }
        getSpiritOpt(obj,"lookAt")?.also { lookAt=it }
        getSpiritOpt(obj,"lookIn")?.also { lookIn=it }

        val type=SpecialSpirit.SpecialType(
                cast(manifestAt,"manifestAt"),
                cast(manifestIn,"manifestIn"),
                cast(lookAt,"lookAt"),
                cast(lookIn,"lookIn"),
                name, color
        )

        HexlinkRegistry.register(HexlinkRegistry.SPECIAL_SPIRIT, id, type)
    }

}