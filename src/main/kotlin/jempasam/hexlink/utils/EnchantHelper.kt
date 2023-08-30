package jempasam.hexlink.utils

import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.EnchantmentLevelEntry
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items

object EnchantHelper {
    fun enchant(target: ItemStack, enchantment: Enchantment, level: Int): ItemStack?{
        if (target.isOf(Items.BOOK)) {
            val ret=Items.ENCHANTED_BOOK.defaultStack
            EnchantedBookItem.addEnchantment(target, EnchantmentLevelEntry(enchantment,level))
            return ret
        }
        else if(enchantment.isAcceptableItem(target)){
            return target.copy().apply {
                addEnchantment(enchantment, level)
            }
        }
        else return null
    }

    fun removeEnchantment(target: ItemStack, enchantment: Enchantment): ItemStack{
        if (target.isOf(Items.ENCHANTED_BOOK)) {
            return Items.BOOK.defaultStack
        }
        else{
            return target.copy().apply {
                val enchants=EnchantmentHelper.get(target)
                enchants.remove(enchantment)
                EnchantmentHelper.set(enchants,this)
            }
        }
    }
}