package jempasam.hexlink.item

import at.petrak.hexcasting.common.items.ItemFocus
import at.petrak.hexcasting.xplat.IXplatAbstractions
import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.item.greatfocus.*
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

    val PotionFocus=make("potion_focus", GreatFocusItem(epicProps(), PotionExtracter))
    val BlockFocus=make("block_focus", GreatFocusItem(epicProps(), BlockExtracter))
    val EntityFocus=make("entity_focus", GreatFocusItem(epicProps(), EntityExtracter))
    val ItemFocus=make("item_focus", GreatFocusItem(epicProps(), ItemExtracter))
    val UltimateFocus=make("ultimate_focus", GreatFocusItem(epicProps(), EverythingExtracter))

    var UpgradedBook=make("upgraded_book", UpgradedBookItem(epicProps()))
    var FocusCollar=make("focus_collar", ItemFocus(simpleProps()))
    var MixedPigment=make("mixed_pigment", MixedPigmentItem(stackableProps()))

    fun epicProps(): Item.Settings{
        return Item.Settings()
                .group(IXplatAbstractions.INSTANCE.tab)
                .maxCount(1)
                .rarity(Rarity.EPIC)
    }

    fun simpleProps(): Item.Settings{
        return Item.Settings()
                .group(IXplatAbstractions.INSTANCE.tab)
                .maxCount(1)
                .rarity(Rarity.COMMON)
    }

    fun stackableProps(): Item.Settings{
        return Item.Settings()
                .group(IXplatAbstractions.INSTANCE.tab)
                .maxCount(64)
                .rarity(Rarity.COMMON)
    }
}