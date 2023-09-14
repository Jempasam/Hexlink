package jempasam.hexlink.spirit.extractor

import jempasam.hexlink.spirit.ItemSpirit
import jempasam.hexlink.spirit.StackHelper
import net.minecraft.entity.Entity
import net.minecraft.item.BlockItem
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import kotlin.math.max

object ItemExtractor : SpiritExtractor<ItemSpirit> {



    override fun extract(caster: ServerPlayerEntity?, target: Entity): SpiritExtractor.ExtractionResult<ItemSpirit> {
        val worldstack= StackHelper.stack(caster, target)
        val stack= worldstack?.stack
        if(stack!=null && stack.item !is BlockItem){
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