package jempasam.hexlink.item

import at.petrak.hexcasting.api.item.IotaHolderItem
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.NullIota
import at.petrak.hexcasting.api.utils.containsTag
import at.petrak.hexcasting.common.items.ItemFocus
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import jempasam.hexlink.data.HexlinkConfiguration
import jempasam.hexlink.iota.SpiritIota
import jempasam.hexlink.item.functionnality.ExtractorItem
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.utils.NbtHelper
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.Entity
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.collection.DefaultedList
import net.minecraft.world.World

class SpiritContainerItem(settings: Settings) : Item(settings), IotaHolderItem, ExtractorItem {

    override fun writeDatum(stack: ItemStack, datum: Iota?) {}

    override fun canWrite(stack: ItemStack, datum: Iota?): Boolean = false

    override fun emptyIota(stack: ItemStack?): Iota = NullIota()

    override fun readIotaTag(stack: ItemStack): NbtCompound?{
        return getSpiritIota(stack)?.let { HexIotaTypes.serialize(it) }
    }

    override fun readIota(stack: ItemStack, world: ServerWorld): Iota? {
        return getSpiritIota(stack)
    }

    fun getSpirit(stack: ItemStack): Spirit?{
        return stack.nbt?.getCompound("content")?.let { NbtHelper.readSpirit(it) }
    }

    fun setSpirit(stack: ItemStack, spirit: Spirit){
        stack.orCreateNbt.put("content",NbtHelper.writeSpirit(spirit))
    }

    fun getSpiritIota(stack: ItemStack): SpiritIota?{
        return getSpirit(stack)
                ?.let{ SpiritIota(it) }
    }



    override fun getTranslationKey(stack: ItemStack): String {
        return super.getTranslationKey(stack) + if (stack.containsTag(ItemFocus.TAG_DATA)) ".filled" else ""
    }

    override fun getName(stack: ItemStack): Text{
        val spirit=getSpirit(stack)
        if(spirit!=null)return Text.translatable(translationKey, spirit.getName())
        else return getExtractorName(stack)
    }

    override fun appendTooltip(pStack: ItemStack, pLevel: World?, pTooltipComponents: MutableList<Text>, pIsAdvanced: TooltipContext) {
        appendExtractorTooltip(pStack, pTooltipComponents)
        getSpiritIota(pStack)?. apply{ pTooltipComponents.add(this.getSpirit().getName()) }
    }

    override fun extractFrom(stack: ItemStack, target: Entity): ExtractorItem.ExtractionResult{
        getExtractor(stack)?.apply {
            if(canExtract(target)){
                val extracted=extract(target)
                val success_rate=HexlinkConfiguration.extractor_settings.get(this)?.success_rate ?: 0.01f
                if(Math.random()<success_rate*extracted.count){
                    setSpirit(stack, extracted.spirit)
                    consume(target)
                    return ExtractorItem.ExtractionResult.SUCCESS
                }
                else{
                    consume(target)
                    return ExtractorItem.ExtractionResult.UNLUCKY
                }
            }
        }
        return ExtractorItem.ExtractionResult.FAIL
    }

    override fun canExtractFrom(stack: ItemStack, target: Entity): Boolean{
        return getExtractor(stack)?.canExtract(target) ?: false && !isFilled(stack)
    }



    fun isFilled(stack: ItemStack): Boolean{
        return readIotaTag(stack)!=null
    }

    override fun appendStacks(group: ItemGroup?, stacks: DefaultedList<ItemStack>) {
        if (isIn(group)) appendStacks(this,stacks)
    }



}