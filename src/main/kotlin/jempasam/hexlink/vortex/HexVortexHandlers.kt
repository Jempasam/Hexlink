package jempasam.hexlink.vortex

import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.HexlinkRegistry
import jempasam.hexlink.spirit.BlockSpirit
import jempasam.hexlink.spirit.ItemSpirit
import net.minecraft.block.Blocks
import net.minecraft.item.Items
import net.minecraft.recipe.RecipeType
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object HexVortexHandlers {
    private fun <T: HexVortexHandler>register(id: String, handler: T): T{
        Registry.register(HexlinkRegistry.HEXVORTEX_HANDLER, Identifier(HexlinkMod.MODID,id), handler)
        return handler
    }

    //TODO Test smelting
    val SMELTING_VORTEX_HANDLER=register("smelting_furnace", CookingVortexHandler(BlockSpirit(Blocks.FURNACE), RecipeType.SMELTING, 1f))
    val FLINT_VORTEX_HANDLER=register("burning_flint", BurningVortexHandler(ItemSpirit(Items.FLINT_AND_STEEL), 1f))

    init {
        VortexRecipeHelper.generateHandlerMaps()
    }
}