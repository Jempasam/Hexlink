package jempasam.hexlink.data

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.HexlinkRegistry
import jempasam.hexlink.spirit.SpecialSpirit
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.utils.NbtHelper
import jempasam.hexlink.utils.asNBT
import jempasam.hexlink.vortex.VortexRecipeHelper
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.minecraft.nbt.NbtCompound
import net.minecraft.resource.JsonDataLoader
import net.minecraft.resource.ResourceManager
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.profiler.Profiler

object SpecialSpiritDataLoader: JsonDataLoader(Gson(),"special_spirits"), IdentifiableResourceReloadListener {
    override fun apply(prepared: MutableMap<Identifier, JsonElement>, manager: ResourceManager, profiler: Profiler) {
        for(entry in prepared){
            try{
                val jsonObject=entry.value.asJsonObject

                fun getSpiritOpt(obj: JsonObject, name: String): Spirit? {
                    return obj.get(name)
                            ?.asNBT()
                            ?.let { if(it is NbtCompound) NbtHelper.readSpirit(it) else null }
                }

                fun cast(spirit: Spirit?, name: String): Spirit{
                    return spirit ?: throw JsonParseException("Missing or invalid \"$name\"")
                }
                val name=jsonObject.get("name")
                        ?.let { Text.Serializer.fromJson(it) }
                        ?: throw JsonParseException("Missing or invalid \"name\"")

                val color=JsonHelper.getInt(jsonObject,"color")

                var manifestAt: Spirit?=null
                var manifestIn: Spirit?=null
                var lookAt: Spirit?=null
                var lookIn: Spirit?=null

                getSpiritOpt(jsonObject,"all")?.also {
                    manifestAt=it
                    manifestIn=it
                    lookAt=it
                    lookIn=it
                }

                getSpiritOpt(jsonObject,"manifest")?.also {
                    manifestAt=it
                    manifestIn=it
                }

                getSpiritOpt(jsonObject,"look")?.also {
                    lookAt=it
                    lookIn=it
                }

                getSpiritOpt(jsonObject,"at")?.also {
                    manifestAt=it
                    lookAt=it
                }

                getSpiritOpt(jsonObject,"in")?.also {
                    manifestIn=it
                    lookIn=it
                }

                getSpiritOpt(jsonObject,"manifestAt")?.also { manifestAt=it }
                getSpiritOpt(jsonObject,"manifestIn")?.also { manifestIn=it }
                getSpiritOpt(jsonObject,"lookAt")?.also { lookAt=it }
                getSpiritOpt(jsonObject,"lookIn")?.also { lookIn=it }

                val type=SpecialSpirit.SpecialType(
                        cast(manifestAt,"manifestAt"),
                        cast(manifestIn,"manifestIn"),
                        cast(lookAt,"lookAt"),
                        cast(lookIn,"lookIn"),
                        name, color
                )

                HexlinkRegistry.register(HexlinkRegistry.SPECIAL_SPIRIT, entry.key, type)

            }catch (e: Exception){
                HexlinkMod.logger.error("In special spirit \"${entry.key}\", ${e.message}")
            }
        }
        VortexRecipeHelper.generateHandlerMaps()
    }

    override fun getFabricId(): Identifier = Identifier(HexlinkMod.MODID,"special_spirit_loader")
}