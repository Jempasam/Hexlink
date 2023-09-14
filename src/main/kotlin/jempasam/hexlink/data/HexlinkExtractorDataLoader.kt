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

class HexlinkExtractorDataLoader : JsonDataLoader(Gson(),"hexlink_extractors"), IdentifiableResourceReloadListener {
    override fun apply(prepared: MutableMap<Identifier, JsonElement>, manager: ResourceManager, profiler: Profiler) {
        for(entry in prepared){
            val extractor=HexlinkRegistry.EXTRACTOR.get(entry.key)
            val obj=entry.value
            if(extractor!=null && obj is JsonObject){
                HexlinkConfiguration.extractor_settings.put(extractor,HexlinkConfiguration.ExtractorSettings(
                        obj.get("soul_count")?.asInt ?: 1,
                        obj.get("extraction_media_cost")?.asInt ?: 2
                ))
            }
        }
    }

    override fun getFabricId(): Identifier = Identifier(HexlinkMod.MODID,"extractor_loader")
}