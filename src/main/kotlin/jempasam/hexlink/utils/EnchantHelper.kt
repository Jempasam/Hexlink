package jempasam.hexlink.utils

import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.EnchantmentLevelEntry
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items

object EnchantHelper {

    fun canEnchant(target: ItemStack, enchantment: Enchantment, level: Int): Boolean{
        // Check acceptable
        if(!enchantment.isAcceptableItem(target))return false

        val enchants= EnchantmentHelper.get(target)

        // Check incompatible
        if(enchants.keys.any { !enchantment.canCombine(it) })return false

        if(level>enchantment.maxLevel)return false

        // Already present and with a incompatible level
        val already= enchants[enchantment]
        if(already!=null && already>level && already+1<enchantment.maxLevel)return false

        return true
    }

    fun enchant(target: ItemStack, enchantment: Enchantment, level: Int): ItemStack?{
        if (target.isOf(Items.BOOK)) {
            val ret=Items.ENCHANTED_BOOK.defaultStack
            val finalLevel= makeLevel(EnchantmentHelper.get(target).getOrDefault(enchantment,0),level)
            EnchantedBookItem.addEnchantment(target, EnchantmentLevelEntry(enchantment,finalLevel))
            return ret
        }
        return target.copy().apply {
            val finalLevel= makeLevel(EnchantmentHelper.getLevel(enchantment,target),level)
            addEnchantment(enchantment, level)
        }
    }

    fun makeLevel(first: Int, second: Int): Int{
        if(first==second)return first+1
        else return Math.max(first,second)
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