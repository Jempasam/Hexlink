package jempasam.hexlink.utils

import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtList

object EnchantHelper {

    fun copy(stack: ItemStack, isEnchanted: Boolean): ItemStack{
        if(isEnchanted){
            if(stack.isOf(Items.BOOK)) return Items.ENCHANTED_BOOK.defaultStack
            else return stack.copy()
        }
        else{
            if(stack.isOf(Items.ENCHANTED_BOOK)) return Items.BOOK.defaultStack
            else return stack.copy()
        }
    }

    fun mapToNbt(enchantments: Map<Enchantment, Int>): NbtList{
        val ret=NbtList()
        for((enchantment,level) in enchantments){
            if(level<=0)continue
            ret.add(EnchantmentHelper.createNbt(EnchantmentHelper.getEnchantmentId(enchantment),level))
        }
        return ret
    }

    fun getNbt(target:ItemStack): NbtList{
        if(target.isOf(Items.ENCHANTED_BOOK)) return EnchantedBookItem.getEnchantmentNbt(target)
        else return target.enchantments
    }

    fun get(target: ItemStack): MutableMap<Enchantment, Int>{
        return EnchantmentHelper.fromNbt(getNbt(target))
    }

    fun putNbt(target: ItemStack, nbt: NbtList){
        if(nbt.isEmpty()){
            if(target.isOf(Items.ENCHANTED_BOOK)) target.nbt?.remove(EnchantedBookItem.STORED_ENCHANTMENTS_KEY)
            else target.nbt?.remove("Enchantments")
        }
        else{
            if(target.isOf(Items.ENCHANTED_BOOK)) target.orCreateNbt.put(EnchantedBookItem.STORED_ENCHANTMENTS_KEY,nbt)
            else target.orCreateNbt.put("Enchantments",nbt)
        }
    }

    fun put(target: ItemStack, enchantments: Map<Enchantment, Int>){
        putNbt(target, mapToNbt(enchantments))
    }

    fun canEnchant(target: ItemStack, enchantment: Enchantment, level: Int): Boolean{
        // Check acceptable
        if(!enchantment.isAcceptableItem(target))return false

        val enchants= EnchantmentHelper.get(target)

        // Check incompatible
        if(enchants.keys.any { enchantment!=it && !enchantment.canCombine(it)  })return false

        if(level>enchantment.maxLevel)return false

        // Already present and with a incompatible level
        val already= enchants[enchantment] ?: 0
        if(level<=already)return false

        return true
    }

    fun enchant(target: ItemStack, enchantment: Enchantment, level: Int): ItemStack?{
        val enchants= get(target)
        enchants[enchantment]= Math.max(level,enchants[enchantment]?:0)
        val stack=copy(target,enchants.count()>0)
        put(stack,enchants)
        return stack
    }

    fun removeEnchantment(target: ItemStack, enchantment: Enchantment): ItemStack{
        val enchants= get(target)
        enchants.remove(enchantment)
        val stack=copy(target, enchants.isNotEmpty())
        put(stack,enchants)
        return stack
    }
}