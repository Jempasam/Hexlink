package jempasam.hexlink.item

import at.petrak.hexcasting.api.item.IotaHolderItem
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.NullIota
import at.petrak.hexcasting.api.utils.containsTag
import at.petrak.hexcasting.common.items.ItemFocus
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import jempasam.hexlink.HexlinkRegistry
import jempasam.hexlink.gamerule.HexlinkGamerules
import jempasam.hexlink.iota.SpiritIota
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.extracter.SpiritExtractor
import jempasam.hexlink.utils.NbtHelper
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.Entity
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtString
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList
import net.minecraft.world.World

class SpiritExtracterItem(settings: Settings) : Item(settings), IotaHolderItem {

    override fun writeDatum(stack: ItemStack, datum: Iota?) {}

    override fun canWrite(stack: ItemStack, datum: Iota?): Boolean = false

    override fun emptyIota(stack: ItemStack?): Iota = NullIota()

    override fun readIotaTag(stack: ItemStack): NbtCompound?{
        return getSpiritIota(stack)?.let { HexIotaTypes.serialize(it) }
    }

    override fun readIota(stack: ItemStack, world: ServerWorld): Iota? {
        return getSpiritIota(stack)
    }



    fun setExtractor(stack: ItemStack, extractor: SpiritExtractor<*>){
        val id=HexlinkRegistry.SPIRIT_EXTRACTER.getId(extractor)
        stack.orCreateNbt.put("extractor", NbtString.of(id?.toString() ?: "none"))
    }

    fun getExtractor(stack: ItemStack): SpiritExtractor<*>?{
        val extractor=stack.nbt?.getString("extractor") ?: ""
        if(extractor.isEmpty())return null
        else return HexlinkRegistry.SPIRIT_EXTRACTER.get(Identifier(extractor))
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

    override fun getName(stack: ItemStack): Text {
        val extractor=getExtractor(stack)
        if(extractor!=null)return extractor.getExtractedName().copy().append(" ").append(super.getName(stack))
        else return super.getName(stack)
    }

    override fun appendTooltip(pStack: ItemStack, pLevel: World?, pTooltipComponents: MutableList<Text>, pIsAdvanced: TooltipContext) {
        getExtractor(pStack)?.apply { pTooltipComponents.add(getExtractedName().copy().setStyle(extractorStyle)) }
        getSpiritIota(pStack)?. apply{ pTooltipComponents.add(this.getSpirit().getName().copy().setStyle(extractorStyle)) }
    }

    fun extractFrom(stack: ItemStack, target: Entity): ExtractionResult{
        getExtractor(stack)?.apply {
            if(canExtract(target)){
                val extracted=extract(target)
                println("Extract Count "+extracted.count+" of "+extracted.spirit.toString())
                if(Math.random()<target.world.gameRules.get(HexlinkGamerules.EXTRACTION_PROBABILITY).get()*extracted.count){
                    setSpirit(stack, extracted.spirit)
                    println("Success")
                    return ExtractionResult.SUCCESS
                }
                else{
                    println("Unlucky")
                    return ExtractionResult.UNLUCKY
                }
            }
        }
        println("Fail")
        return ExtractionResult.FAIL
    }

    fun canExtractFrom(stack: ItemStack, target: Entity): Boolean{
        return getExtractor(stack)?.canExtract(target) ?: false && !isFilled(stack)
    }

    enum class ExtractionResult{
        UNLUCKY,
        FAIL,
        SUCCESS
    }

    fun isFilled(stack: ItemStack): Boolean{
        return readIotaTag(stack)!=null
    }

    override fun appendStacks(group: ItemGroup?, stacks: DefaultedList<ItemStack?>) {
        if (isIn(group)) {
            for(extractor in HexlinkRegistry.SPIRIT_EXTRACTER.entrySet){
                val stack=defaultStack
                setExtractor(stack, extractor.value)
                stacks.add(stack)
            }
        }
    }

    companion object{
        private val extractorStyle=Style.EMPTY.withItalic(true).withColor(DyeColor.MAGENTA.signColor)
    }

}