package jempasam.hexlink.data

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.HexlinkRegistry
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.minecraft.resource.JsonDataLoader
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import net.minecraft.util.profiler.Profiler

class HexlinkSpiritDataLoader : JsonDataLoader(Gson(),"hexlink_spirits"), IdentifiableResourceReloadListener {
    override fun apply(prepared: MutableMap<Identifier, JsonElement>, manager: ResourceManager, profiler: Profiler) {
        for(entry in prepared){
            val spirit=HexlinkRegistry.SPIRIT.get(entry.key)
            val obj=entry.value
            if(spirit!=null && obj is JsonObject){
                HexlinkConfiguration.spirit_settings.put(spirit,HexlinkConfiguration.SpiritSettings(
                        obj.get("use_soul")?.asBoolean ?: true,
                        obj.get("media_cost")?.asInt ?: 5
                ))
            }
        }
    }

    override fun getFabricId(): Identifier = Identifier(HexlinkMod.MODID,"spirit_loader")
}