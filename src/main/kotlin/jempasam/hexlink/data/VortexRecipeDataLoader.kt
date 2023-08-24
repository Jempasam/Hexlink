package jempasam.hexlink.data

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.HexlinkRegistry
import jempasam.hexlink.vortex.VortexRecipeHelper
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.minecraft.resource.JsonDataLoader
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.profiler.Profiler

object VortexRecipeDataLoader: JsonDataLoader(Gson(),"vortex_recipes"), IdentifiableResourceReloadListener {
    override fun apply(prepared: MutableMap<Identifier, JsonElement>, manager: ResourceManager, profiler: Profiler) {
        for(entry in prepared){
            try{
                val jsonObject=entry.value.asJsonObject
                val id=JsonHelper.getString(jsonObject,"type")
                val type=HexlinkRegistry.HEXVORTEX_HANDLER_SERIALIZER.get(Identifier(id))
                type ?: throw JsonParseException("$id is not a valid vortex recipe type")
                HexlinkRegistry.register(HexlinkRegistry.HEXVORTEX_HANDLER, entry.key, type.serialize(jsonObject))
            }catch (e: Exception){
                HexlinkMod.logger.error("In vortex recipe \"${entry.key}\", ${e.message}")
            }
        }
        VortexRecipeHelper.generateHandlerMaps()
    }

    override fun getFabricId(): Identifier = Identifier(HexlinkMod.MODID,"vortex_recipe_loader")
}