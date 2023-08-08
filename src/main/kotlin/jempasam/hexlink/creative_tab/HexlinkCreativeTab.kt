package jempasam.hexlink.creative_tab

import jempasam.hexlink.item.HexlinkItems
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack

object HexlinkCreativeTab {
    val HEXLINK: ItemGroup = object : ItemGroup(24923, "hexlink") {
        override fun createIcon(): ItemStack {
            return HexlinkItems.UpgradedBook.defaultStack
        }
    }
}