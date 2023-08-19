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

    val BURNING_VORTEX_HANDLER=register("burning", BurningVortexHandler())
}