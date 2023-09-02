package jempasam.hexlink.creative_tab

import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.item.HexlinkItems
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.registry.Registry

object HexlinkCreativeTab {

    val MAIN_TAB: ItemGroup = FabricItemGroupBuilder.create(Identifier(HexlinkMod.MODID,"hexlink"))
            .icon { HexlinkItems.UpgradedBook.defaultStack }
            .appendItems{ list, group ->
                if( list is DefaultedList<ItemStack>){
                    for(item in Registry.ITEM) if(item.group==group) item.appendStacks(group,list)
                }
                list.sortBy { it.item.javaClass.hashCode() }
            }
            .build()

}