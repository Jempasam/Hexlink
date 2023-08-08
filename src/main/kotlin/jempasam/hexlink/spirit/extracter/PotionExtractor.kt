package jempasam.hexlink.spirit.extracter

import jempasam.hexlink.spirit.PotionSpirit
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.item.Items
import net.minecraft.potion.PotionUtil
import net.minecraft.text.Text

object PotionExtractor : SpiritExtractor<PotionSpirit> {
    override fun extract(target: Entity): SpiritExtractor.ExtractionResult<PotionSpirit> {
        target as ItemEntity
        val effect= PotionUtil.getPotionEffects(target.stack).get(0)
        return result(PotionSpirit(effect.effectType), Math.max(1,effect.duration/3600)*(effect.amplifier+1))
    }

    override fun canExtract(target: Entity): Boolean {
        if(target is ItemEntity && target.stack.item== Items.POTION){
            val effects= PotionUtil.getPotionEffects(target.stack)
            if(!effects.isEmpty())return true
        }
        return false
    }

    override fun getExtractedName(): Text {
        return Text.translatable("hexlink.extractor.potion")
    }
}