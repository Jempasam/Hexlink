package jempasam.hexlink.data

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import jempasam.hexlink.HexlinkMod
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.minecraft.resource.JsonDataLoader
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import net.minecraft.util.profiler.Profiler

abstract class JsonEntryDataLoader(private val name: String): JsonDataLoader(Gson(),name), IdentifiableResourceReloadListener {
    override fun apply(prepared: MutableMap<Identifier, JsonElement>, manager: ResourceManager, profiler: Profiler) {
        before()
        for(entry in prepared){
            try{
                val jsonObject=entry.value.asJsonObject
                apply(entry.key,jsonObject)
            }catch (e: Exception){
                HexlinkMod.logger.error("In $name \"${entry.key}\", ${e.message}")
            }
        }
        after()
    }

    protected abstract fun apply(id: Identifier, obj: JsonObject)

    protected open fun after(){ }

    protected open fun before(){ }

    override fun getFabricId(): Identifier = Identifier(HexlinkMod.MODID,"${name}_loader")
}