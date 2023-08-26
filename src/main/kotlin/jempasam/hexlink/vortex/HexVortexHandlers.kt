package jempasam.hexlink.vortex

import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.HexlinkRegistry
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object HexVortexHandlers {
    private fun <T: HexVortexHandler>register(id: String, handler: T): T{
        Registry.register(HexlinkRegistry.HEXVORTEX_HANDLER, Identifier(HexlinkMod.MODID,id), handler)
        return handler
    }

    private fun <T: HexVortexHandler>register(id: String, handler: HexVortexHandler.Serializer<T>): HexVortexHandler.Serializer<T>{
        Registry.register(HexlinkRegistry.HEXVORTEX_HANDLER_SERIALIZER, Identifier(HexlinkMod.MODID,id), handler)
        return handler
    }


    val COOKING_SERIALIZER= register("cooking", CookingVortexHandler.SERIALIZER)
    val BURNING_SERIALIZER= register("burning", BurningVortexHandler.SERIALIZER)
    val PATTERN_SERIALIZER= register("pattern", PatternVortexHandler.SERIALIZER)
    val COMPOSTING_SERIALIZER= register("composting", CompostingVortexHandler.SERIALIZER)

}