package jempasam.hexlink.spirit.extracter

import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.spirit.ItemSpirit
import net.minecraft.entity.Entity
import net.minecraft.tag.TagKey
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object ItemExtractor : SpiritExtractor<ItemSpirit> {

    val NOT_EXTRACTABLE=TagKey.of(Registry.ITEM_KEY, Identifier(HexlinkMod.MODID, "not_extractable"))

    override fun canExtract(target: Entity): Boolean {
        val stack=ExtractorHelper.stack(target)
        return stack!=null && !stack.isIn(NOT_EXTRACTABLE)
    }

    override fun extract(target: Entity): SpiritExtractor.ExtractionResult<ItemSpirit> {
        val stack=ExtractorHelper.stackOrThrow(target)
        return result(ItemSpirit(stack.item), stack.count*Math.max(stack.maxDamage/2,1))
    }

    override fun consume(target: Entity) {
        ExtractorHelper.killStack(target)
    }

    override fun getExtractedName(): Text {
        return Text.translatable("hexlink.extractor.item")
    }

    override fun getColor(): Int {
        return DyeColor.BROWN.fireworkColor
    }

}