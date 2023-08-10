package jempasam.hexlink.item.functionnality

import jempasam.hexlink.HexlinkRegistry
import jempasam.hexlink.spirit.extracter.SpiritExtractor
import net.minecraft.entity.Entity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtString
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList

interface ExtractorItem {
    fun extractFrom(stack: ItemStack, target: Entity): ExtractionResult

    fun canExtractFrom(stack: ItemStack, target: Entity): Boolean

    fun setExtractor(stack: ItemStack, extractor: SpiritExtractor<*>){
        val id= HexlinkRegistry.SPIRIT_EXTRACTER.getId(extractor)
        stack.orCreateNbt.put("extractor", NbtString.of(id?.toString() ?: "none"))
    }

    fun getExtractor(stack: ItemStack): SpiritExtractor<*>?{
        val extractor=stack.nbt?.getString("extractor") ?: ""
        if(extractor.isEmpty())return null
        else return HexlinkRegistry.SPIRIT_EXTRACTER.get(Identifier(extractor))
    }

    fun appendStacks(item :Item, stacks: DefaultedList<ItemStack>) {
        for(extractor in HexlinkRegistry.SPIRIT_EXTRACTER.entrySet){
            val stack=item.defaultStack
            setExtractor(stack, extractor.value)
            stacks.add(stack)
        }
    }

    fun getExtractorName(stack: ItemStack): Text {
        val extractor=getExtractor(stack)
        if(extractor!=null)return Text.translatable(stack.translationKey, extractor.getExtractedName())
        else return Text.translatable(stack.translationKey+".none")
    }

    fun appendExtractorTooltip(pStack: ItemStack, pTooltipComponents: MutableList<Text>) {
        getExtractor(pStack)?.apply { pTooltipComponents.add(getExtractedName().copy().setStyle(extractorStyle)) }
    }

    enum class ExtractionResult{
        UNLUCKY,
        FAIL,
        SUCCESS
    }

    companion object{
        val extractorStyle= Style.EMPTY.withItalic(true).withColor(DyeColor.MAGENTA.signColor)
    }
}