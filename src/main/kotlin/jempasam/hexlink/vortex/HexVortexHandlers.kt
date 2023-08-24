package jempasam.hexlink.vortex

import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.HexlinkRegistry
import jempasam.hexlink.spirit.BlockSpirit
import jempasam.hexlink.spirit.ItemSpirit
import net.minecraft.block.Blocks
import net.minecraft.item.Items
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

    val SMELTING_VORTEX_HANDLER=register("smelting_furnace", CookingVortexHandler(BlockSpirit(Blocks.FURNACE), 1f))
    val FLINT_VORTEX_HANDLER=register("burning_flint", BurningVortexHandler(ItemSpirit(Items.FLINT_AND_STEEL), BlockSpirit(Blocks.FIRE), 1f))

}