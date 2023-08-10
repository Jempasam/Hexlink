package jempasam.hexlink.data

import com.google.gson.Gson
import com.google.gson.JsonElement
import net.minecraft.resource.JsonDataLoader
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import net.minecraft.util.profiler.Profiler

class HexlinkDataLoader : JsonDataLoader(Gson(),"hexlin_spirit") {
    override fun apply(prepared: MutableMap<Identifier, JsonElement>, manager: ResourceManager, profiler: Profiler) {
        TODO("Not yet implemented")
    }
}