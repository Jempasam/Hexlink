package jempasam.hexlink.item.functionnality

import jempasam.hexlink.HexlinkRegistry
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.extracter.SpiritExtractor
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtString
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList

interface ExtractorItem {

    fun extractFrom(stack: ItemStack, caster: PlayerEntity?, target: Entity): SpiritExtractor.ExtractionResult<*>
        = getExtractor(stack)?.extract(caster, target) ?: SpiritExtractor.noResult<Spirit>()

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

    fun getExtractorName(stack: ItemStack): Text
        = getExtractor(stack)?.let { Text.translatable(stack.translationKey, it.getName())  }
                ?: Text.translatable(stack.translationKey+".none")

    fun appendExtractorTooltip(pStack: ItemStack, pTooltipComponents: MutableList<Text>)
        = getExtractor(pStack)?.apply { pTooltipComponents.add(getName().copy().setStyle(extractorStyle)) }

    companion object{
        val extractorStyle= Style.EMPTY.withItalic(true).withColor(DyeColor.MAGENTA.signColor)
    }
}