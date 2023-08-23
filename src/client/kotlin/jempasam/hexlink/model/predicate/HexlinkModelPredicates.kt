package jempasam.hexlink.model.predicate

import at.petrak.hexcasting.api.item.IotaHolderItem
import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.item.HexlinkItems
import jempasam.hexlink.item.functionnality.ItemSpiritSource
import net.minecraft.client.item.ModelPredicateProviderRegistry
import net.minecraft.util.Identifier

object HexlinkModelPredicates {
    fun registerItemPredicates(){
        for(item_entry in HexlinkItems.items){
            if(item_entry.value is IotaHolderItem){
                ModelPredicateProviderRegistry.register(item_entry.value, Identifier(HexlinkMod.MODID,"is_filled"), FilledIotaHolderPredicate)
            }
            if(item_entry.value is ItemSpiritSource){
                ModelPredicateProviderRegistry.register(item_entry.value, Identifier(HexlinkMod.MODID,"source_filled"), FilledSpiritSourcePredicate)
            }
        }
    }
}