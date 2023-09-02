package jempasam.hexlink.recipe.vortex

import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.HexlinkRegistry
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object HexVortexHandlers {
    private fun <T: HexVortexHandler>register(id: String, handler: T): T{
        Registry.register(HexlinkRegistry.HEXVORTEX_HANDLER, Identifier(HexlinkMod.MODID,id), handler)
        return handler
    }

    private fun <T: HexVortexHandler>register(id: String, handler: HexVortexHandler.Parser<T>): HexVortexHandler.Parser<T> {
        Registry.register(HexlinkRegistry.HEXVORTEX_HANDLER_PARSER, Identifier(HexlinkMod.MODID,id), handler)
        return handler
    }


    val COOKING_SERIALIZER= register("smelting", SmeltingVortexHandler.PARSER)
    val BURNING_SERIALIZER= register("burning", BurningVortexHandler.PARSER)
    val PATTERN_SERIALIZER= register("pattern", PatternVortexHandler.PARSER)
    val COMPOSTING_SERIALIZER= register("composting", CompostingVortexHandler.PARSER)
    val SIMPLE_SERIALIZER= register("simple", SimpleVortexHandler.PARSER)

}