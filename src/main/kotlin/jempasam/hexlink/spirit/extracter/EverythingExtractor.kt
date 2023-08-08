package jempasam.hexlink.spirit.extracter

import jempasam.hexlink.spirit.Spirit
import net.minecraft.entity.Entity
import net.minecraft.text.Text
import net.minecraft.util.math.ColorHelper

object EverythingExtractor : SpiritExtractor<Spirit> {
    override fun extract(target: Entity): SpiritExtractor.ExtractionResult<Spirit> {
        if(PotionExtractor.canExtract(target))return set_type(PotionExtractor.extract(target))
        else if(BlockExtractor.canExtract(target))return set_type(BlockExtractor.extract(target))
        else if(ItemExtractor.canExtract(target))return set_type(ItemExtractor.extract(target))
        else return set_type(EntityExtractor.extract(target))
    }

    override fun canExtract(target: Entity): Boolean {
        return  PotionExtractor.canExtract(target) ||
                BlockExtractor.canExtract(target) ||
                ItemExtractor.canExtract(target) ||
                EntityExtractor.canExtract(target)
    }

    fun <T: Spirit>set_type(from: SpiritExtractor.ExtractionResult<T>): SpiritExtractor.ExtractionResult<Spirit>{
        return result(from.spirit, from.count)
    }

    override fun getExtractedName(): Text {
        return Text.translatable("hexlink.extractor.everything")
    }

    override fun getColor(): Int {
        val red=(Math.sin(System.currentTimeMillis()/1000*Math.PI)*255).toInt()
        val blue=(Math.sin(System.currentTimeMillis()/1000*Math.PI+Math.PI/3)*255).toInt()
        val green=(Math.sin(System.currentTimeMillis()/1000*Math.PI+Math.PI/3*2)*255).toInt()
        return ColorHelper.Argb.getArgb(255, red, green, blue)
    }
}