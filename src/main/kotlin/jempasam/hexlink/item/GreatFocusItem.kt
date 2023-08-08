package jempasam.hexlink.item

import at.petrak.hexcasting.api.item.IotaHolderItem
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.NullIota
import at.petrak.hexcasting.api.utils.containsTag
import at.petrak.hexcasting.api.utils.getCompound
import at.petrak.hexcasting.common.items.ItemFocus
import jempasam.hexlink.HexlinkRegistry
import jempasam.hexlink.gamerule.HexlinkGamerules
import jempasam.hexlink.spirit.extracter.SpiritExtractor
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.Entity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtString
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import net.minecraft.world.World

class GreatFocusItem(settings: Settings) : Item(settings), IotaHolderItem {

    override fun writeDatum(stack: ItemStack, datum: Iota?) {}

    override fun canWrite(stack: ItemStack, datum: Iota?): Boolean = false

    override fun emptyIota(stack: ItemStack?): Iota = NullIota()

    override fun readIotaTag(stack: ItemStack): NbtCompound? = stack.getCompound("content")



    fun setExtractor(stack: ItemStack, extractor: SpiritExtractor<*>){
        val id=HexlinkRegistry.SPIRIT_EXTRACTER.getId(extractor)
        stack.orCreateNbt.put("extractor", NbtString.of(id?.toString() ?: "none"))
    }

    fun getExtractor(stack: ItemStack): SpiritExtractor<*>?{
        val extractor=stack.nbt?.getString("extractor") ?: ""
        if(extractor.isEmpty())return null
        else return HexlinkRegistry.SPIRIT_EXTRACTER.get(Identifier(extractor))
    }



    override fun getTranslationKey(stack: ItemStack): String {
        return super.getTranslationKey(stack) + if (stack.containsTag(ItemFocus.TAG_DATA)) ".filled" else ""
    }

    override fun appendTooltip(pStack: ItemStack, pLevel: World?, pTooltipComponents: MutableList<Text>, pIsAdvanced: TooltipContext) {
        getExtractor(pStack)?.apply { pTooltipComponents.add(getExtractedName().copy().setStyle(extractorStyle)) }
        IotaHolderItem.appendHoverText(this, pStack, pTooltipComponents, pIsAdvanced)
    }

    fun extractFrom(stack: ItemStack, target: Entity): ExtractionResult{
        getExtractor(stack)?.apply {
            if(canExtract(target)){
                val extracted=extract(target)
                if(Math.random()<target.world.gameRules.get(HexlinkGamerules.EXTRACTION_PROBABILITY).get()*extracted.count){
                    stack.orCreateNbt.put("content",extracted.spirit.serialize())
                    return ExtractionResult.SUCCESS
                }
                else return ExtractionResult.UNLUCKY
            }
        }
        return ExtractionResult.FAIL
    }

    fun canExtractFrom(stack: ItemStack, target: Entity): Boolean{
        return getExtractor(stack)?.canExtract(target) ?: false
    }

    enum class ExtractionResult{
        UNLUCKY,
        FAIL,
        SUCCESS
    }

    fun isFilled(stack: ItemStack): Boolean{
        return readIotaTag(stack)!=null
    }

    companion object{
        private val extractorStyle=Style.EMPTY.withBold(true).withColor(DyeColor.MAGENTA.signColor)
    }

}