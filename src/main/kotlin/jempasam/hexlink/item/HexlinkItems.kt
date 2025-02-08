package jempasam.hexlink.item

import at.petrak.hexcasting.common.items.ItemStaff
import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.block.HexlinkBlocks
import jempasam.hexlink.creative_tab.HexlinkCreativeTab
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity
import net.minecraft.util.registry.Registry

object HexlinkItems {

    var items=HashMap<String,Item>()

    fun <T: Item>make(id: String, item: T): T{
        items.put(id, item)
        return item
    }

    fun registerAll(){
        for(entry in items )Registry.register(Registry.ITEM, Identifier(HexlinkMod.MODID, entry.key), entry.value)
    }

    val Crystal= make("crystal", ExtractorCatalyzerItem(epicProps()))
    //val SpirtualStaff=make("spiritual_staff",SpiritualStaffItem(simpleProps()))
    val MixedPigment=make("mixed_pigment", MixedPigmentItem(stackableProps()))

    val Spirit=make("spirit",SingleSpiritContainerItem(stackableProps()))
    val PhilosophicalCrystal=make("philosophical_crystal",InfiniteSpiritItem(epicProps()))
    val HauntedCrystal=make("haunted_crystal",RandomSpiritItem(epicProps().maxDamage(100)))
    val Tablet=make("tablet",SoulContainerItem(simpleProps(), 1, 10))
    val SmallBag=make("small_bag",SoulContainerItem(simpleProps(), 2, 30))
    val MediumBag=make("medium_bag",SoulContainerItem(simpleProps(), 5, 100))
    val BigBag=make("big_bag",SoulContainerItem(simpleProps(), 10, 300))

    val Vortex=make("vortex",BlockItem(HexlinkBlocks.VORTEX, stackableProps()))
    val BigTablet=make("big_tablet",BlockItem(HexlinkBlocks.BIG_TABLET, stackableProps()))

    val TabletStaff=make("tablet_staff", ItemStaff(simpleProps()))
    val SpiritStaff=make("spirit_staff", ItemStaff(simpleProps()))
    val BigTabletStaff=make("big_tablet_on_a_stick", ItemStaff(simpleProps()))
    val PureMediaStaff=make("pure_media_staff", ItemStaff(simpleProps()))

    fun epicProps(): Item.Settings{
        return Item.Settings()
                .group(HexlinkCreativeTab.MAIN_TAB)
                .maxCount(1)
                .rarity(Rarity.EPIC)
    }

    fun simpleProps(): Item.Settings{
        return Item.Settings()
                .group(HexlinkCreativeTab.MAIN_TAB)
                .maxCount(1)
                .rarity(Rarity.COMMON)
    }

    fun forbiddenProps(): Item.Settings{
        return Item.Settings()
                .fireproof()
                .maxCount(1)
                .rarity(Rarity.RARE)
    }

    fun stackableProps(): Item.Settings{
        return Item.Settings()
                .group(HexlinkCreativeTab.MAIN_TAB)
                .maxCount(64)
                .rarity(Rarity.COMMON)
    }
}