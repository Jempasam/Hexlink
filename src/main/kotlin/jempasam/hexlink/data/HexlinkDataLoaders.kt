package jempasam.hexlink.data


import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.resource.ResourceType

object HexlinkDataLoaders {
    fun registerLoaders(){
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(HexlinkSpiritDataLoader())
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(SpecialSpiritDataLoader)
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(VortexRecipeDataLoader)
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(SpecialExtractorDataLoader)
    }
}