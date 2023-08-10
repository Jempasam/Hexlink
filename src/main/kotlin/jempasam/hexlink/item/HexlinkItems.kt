package jempasam.hexlink.item

import at.petrak.hexcasting.common.items.ItemFocus
import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.creative_tab.HexlinkCreativeTab
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity
import net.minecraft.util.registry.Registry

object HexlinkItems {

    var items=HashMap<String,Item>()

    fun make(id: String, item: Item): Item{
        items.put(id, item)
        return item
    }

    fun registerAll(){
        for(entry in items )Registry.register(Registry.ITEM, Identifier(HexlinkMod.MODID, entry.key), entry.value)
    }

    val Tablet=make("tablet", SpiritContainerItem(epicProps()))

    var UpgradedBook=make("upgraded_book", UpgradedBookItem(epicProps()))
    var FocusCollar=make("focus_collar", ItemFocus(simpleProps()))
    var MixedPigment=make("mixed_pigment", MixedPigmentItem(stackableProps()))

    var SmallBag=make("small_bag",SoulContainerItem(simpleProps(), 2, 30))
    var MediumBag=make("medium_bag",SoulContainerItem(simpleProps(), 5, 100))
    var BigBag=make("big_bag",SoulContainerItem(simpleProps(), 10, 300))

    fun epicProps(): Item.Settings{
        return Item.Settings()
                .group(HexlinkCreativeTab.HEXLINK)
                .maxCount(1)
                .rarity(Rarity.EPIC)
    }

    fun simpleProps(): Item.Settings{
        return Item.Settings()
                .group(HexlinkCreativeTab.HEXLINK)
                .maxCount(1)
                .rarity(Rarity.COMMON)
    }

    fun stackableProps(): Item.Settings{
        return Item.Settings()
                .group(HexlinkCreativeTab.HEXLINK)
                .maxCount(64)
                .rarity(Rarity.COMMON)
    }
}