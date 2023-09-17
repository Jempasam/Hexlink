package jempasam.hexlink.item.color

import jempasam.hexlink.item.HexlinkItems
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry

object HexlinkColorProviders {
    fun registerItemColors(){
        ColorProviderRegistry.ITEM.register(MixedPigmentColor(),HexlinkItems.MixedPigment)
        ColorProviderRegistry.ITEM.register(SpiritContainerColor(), HexlinkItems.Tablet)

        ColorProviderRegistry.ITEM.register(ExtractorItemColor(), HexlinkItems.Crystal)
        ColorProviderRegistry.ITEM.register(SpiritContainerColor(), HexlinkItems.SmallBag)
        ColorProviderRegistry.ITEM.register(SpiritContainerColor(), HexlinkItems.MediumBag)
        ColorProviderRegistry.ITEM.register(SpiritContainerColor(), HexlinkItems.BigBag)


        ColorProviderRegistry.ITEM.register(DyeableColor(), HexlinkItems.SpecialStaff)

        ColorProviderRegistry.ITEM.register(SpiritSourceColor(0), HexlinkItems.Spirit)
        ColorProviderRegistry.ITEM.register(SpiritSourceColor(0), HexlinkItems.PhilosophicalCrystal)
        ColorProviderRegistry.ITEM.register(SpiritSourceColor(0), HexlinkItems.HauntedCrystal)
    }
}