package jempasam.hexlink.item

import at.petrak.hexcasting.common.items.ItemFocus
import at.petrak.hexcasting.common.items.storage.ItemFocus
import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.block.HexlinkBlocks
import jempasam.hexlink.creative_tab.HexlinkCreativeTab
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity
import net.minecraft.registry.Registry

object HexlinkItems {

    var items=HashMap<String,Item>()

    fun <T: Item>make(id: String, item: T): T{
        items.put(id, item)
        return item
    }

    fun registerAll(){
        for(entry in items )Registry.register(Registries.ITEM, Identifier(HexlinkMod.MODID, entry.key), entry.value)
    }

    val Crystal= make("crystal", ExtractorCatalyzerItem(epicProps()))

    var UpgradedBook=make("upgraded_book", UpgradedBookItem(epicProps()))
    var SpecialStaff=make("special_staff", SpecialWandItem(simpleProps()))
    //var SpirtualStaff=make("spiritual_staff",SpiritualStaffItem(simpleProps()))
    var MixedPigment=make("mixed_pigment", MixedPigmentItem(stackableProps()))

    var Spirit=make("spirit",SingleSpiritContainerItem(stackableProps()))
    var PhilosophicalCrystal=make("philosophical_crystal",InfiniteSpiritItem(epicProps()))
    var HauntedCrystal=make("haunted_crystal",RandomSpiritItem(epicProps().maxDamage(100)))
    var Tablet=make("tablet",SoulContainerItem(simpleProps(), 1, 10))
    var SmallBag=make("small_bag",SoulContainerItem(simpleProps(), 2, 30))
    var MediumBag=make("medium_bag",SoulContainerItem(simpleProps(), 5, 100))
    var BigBag=make("big_bag",SoulContainerItem(simpleProps(), 10, 300))

    var Vortex=make("vortex",BlockItem(HexlinkBlocks.VORTEX, stackableProps()))
    var BigTablet=make("big_tablet",BlockItem(HexlinkBlocks.BIG_TABLET, stackableProps()))

    fun epicProps(): Item.Settings{
        return Item.Settings()
                .maxCount(1)
                .rarity(Rarity.EPIC)
    }

    fun simpleProps(): Item.Settings{
        return Item.Settings()
                .maxCount(1)
                .rarity(Rarity.COMMON)
    }

    fun stackableProps(): Item.Settings{
        return Item.Settings()
                .maxCount(64)
                .rarity(Rarity.COMMON)
    }
}