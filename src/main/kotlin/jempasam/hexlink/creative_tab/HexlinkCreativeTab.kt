package jempasam.hexlink.creative_tab

import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.item.HexlinkItems
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.item.ItemGroup
import net.minecraft.util.Identifier

object HexlinkCreativeTab {
    val HEXLINK: ItemGroup = FabricItemGroupBuilder.create(Identifier(HexlinkMod.MODID,"hexlink"))
            .icon { HexlinkItems.UpgradedBook.defaultStack }
            .build()

}