package jempasam.hexlink.item.color

import jempasam.hexlink.item.HexlinkItems
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry

object HexlinkColorProviders {
    fun registerItemColors(){
        ColorProviderRegistry.ITEM.register(MixedPigmentColor(),HexlinkItems.MixedPigment)
    }
}