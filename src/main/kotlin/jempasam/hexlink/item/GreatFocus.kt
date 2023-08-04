package jempasam.hexlink.item

import at.petrak.hexcasting.api.item.IotaHolderItem
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.NullIota
import at.petrak.hexcasting.api.utils.containsTag
import at.petrak.hexcasting.api.utils.getCompound
import at.petrak.hexcasting.api.utils.putTag
import at.petrak.hexcasting.common.items.ItemFocus
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.Entity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.text.Text
import net.minecraft.world.World

class GreatFocus<T: Iota>(settings: Item.Settings, val filter: (Entity)->T?) : Item(settings), IotaHolderItem {

    override fun writeDatum(stack: ItemStack, datum: Iota?) {
        if(datum!=null){
            stack.putTag(ItemFocus.TAG_DATA, HexIotaTypes.serialize(datum))
        }
    }

    override fun getTranslationKey(stack: ItemStack): String {
        return super.getTranslationKey(stack) + if (stack.containsTag(ItemFocus.TAG_DATA)) ".filled" else ""
    }

    override fun emptyIota(stack: ItemStack?): Iota? {
        return NullIota()
    }

    override fun readIotaTag(stack: ItemStack): NbtCompound? {
        return stack.getCompound(ItemFocus.TAG_DATA)
    }

    override fun canWrite(stack: ItemStack, datum: Iota?): Boolean {
        return !stack.containsTag(ItemFocus.TAG_DATA) && datum!=null
    }

    override fun appendTooltip(pStack: ItemStack, pLevel: World?, pTooltipComponents: List<Text?>, pIsAdvanced: TooltipContext) {
        IotaHolderItem.appendHoverText(this, pStack, pTooltipComponents, pIsAdvanced)
    }

    fun writeEntity(stack: ItemStack, target: Entity): Boolean{
        val result=filter(target)
        if(result==null)return false
        else{
            writeDatum(stack,result)
            return true
        }
    }

    fun canWriteEntity(stack: ItemStack, target: Entity): Boolean {
        return !stack.containsTag(ItemFocus.TAG_DATA) && filter(target)!=null
    }
}