package jempasam.hexlink.data

import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import jempasam.hexlink.HexlinkRegistry
import jempasam.hexlink.recipe.vortex.VortexRecipeHelper
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper

object VortexRecipeDataLoader: JsonEntryDataLoader("vortex_recipes") {

    override fun before() = HexlinkRegistry.HEXVORTEX_HANDLER.clear()

    override fun apply(id: Identifier, obj: JsonObject) {
        val typeid=JsonHelper.getString(obj,"type")
        val type=HexlinkRegistry.HEXVORTEX_HANDLER_PARSER.get(Identifier(typeid))
        type ?: throw JsonParseException("$typeid is not a valid vortex recipe type")
        HexlinkRegistry.HEXVORTEX_HANDLER.register(id, type.serialize(obj))
    }

    override fun after(){
        HexlinkRegistry.HEXVORTEX_HANDLER.lock()
        VortexRecipeHelper.generateHandlerMaps()
    }

}