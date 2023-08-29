package jempasam.hexlink.spirit.extracter

import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.spirit.ItemSpirit
import jempasam.hexlink.spirit.StackHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.tag.TagKey
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import kotlin.math.max

object ItemExtractor : SpiritExtractor<ItemSpirit> {

    val NOT_EXTRACTABLE=TagKey.of(Registry.ITEM_KEY, Identifier(HexlinkMod.MODID, "not_extractable"))

    override fun extract(caster: PlayerEntity?, target: Entity): SpiritExtractor.ExtractionResult<ItemSpirit> {
        val worldstack= StackHelper.stack(caster, target)
        val stack= worldstack?.stack
        if(stack!=null && stack.item !is BlockItem && !stack.isIn(NOT_EXTRACTABLE)){
            worldstack as StackHelper.WorldStack
            return SpiritExtractor.ExtractionResult(
                    ItemSpirit(stack.item),
                    stack.count* max(stack.maxDamage,1)
            ) { worldstack.killer() }
        }
        else return noResult()
    }

    override fun getName(): Text = Text.translatable("hexlink.extractor.item")

    override fun getColor(): Int = DyeColor.BROWN.fireworkColor

}