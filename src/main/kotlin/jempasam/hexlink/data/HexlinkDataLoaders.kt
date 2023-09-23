package jempasam.hexlink.data


import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier

object HexlinkDataLoaders {

    private fun <T: IdentifiableResourceReloadListener> register(loader: T): Identifier{
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(loader)
        return loader.fabricId
    }


    @JvmStatic val SPIRIT_CONFIG= register(HexlinkSpiritDataLoader())
    @JvmStatic val SPIRITS= register(SpecialSpiritDataLoader)
    @JvmStatic val VORTEX_RECIPES= register(VortexRecipeDataLoader)
    @JvmStatic val EXTRACTORS= register(SpecialExtractorDataLoader)
}