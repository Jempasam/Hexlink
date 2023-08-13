package jempasam.hexlink.spirit.extracter

import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.spirit.ItemSpirit
import jempasam.hexlink.spirit.SpiritHelper
import net.minecraft.entity.Entity
import net.minecraft.tag.TagKey
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object ItemExtractor : SpiritExtractor<ItemSpirit> {

    val NOT_EXTRACTABLE=TagKey.of(Registry.ITEM_KEY, Identifier(HexlinkMod.MODID, "not_extractable"))

    override fun canExtract(target: Entity): Boolean {
        val stack= SpiritHelper.stack(null, target)?.stack
        return stack!=null && !stack.isIn(NOT_EXTRACTABLE)
    }

    override fun extract(target: Entity): SpiritExtractor.ExtractionResult<ItemSpirit> {
        val stack= SpiritHelper.stackOrThrow(null, target).stack
        return result(ItemSpirit(stack.item), stack.count*Math.max(stack.maxDamage/2,1))
    }

    override fun consume(target: Entity) {
        SpiritHelper.stackOrThrow(null, target).killer()
    }

    override fun getExtractedName(): Text {
        return Text.translatable("hexlink.extractor.item")
    }

    override fun getColor(): Int {
        return DyeColor.BROWN.fireworkColor
    }

}