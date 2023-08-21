package jempasam.hexlink.spirit.extracter

import com.google.gson.JsonElement
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.extracter.loaders.SpiritExtractorLoader
import net.minecraft.entity.Entity
import net.minecraft.text.Text
import net.minecraft.util.math.ColorHelper
import kotlin.math.sin

object EverythingExtractor : SpiritExtractor<Spirit> {
    override fun extract(target: Entity): SpiritExtractor.ExtractionResult<Spirit> {
        if(PotionExtractor.canExtract(target)) return set_type(PotionExtractor.extract(target))
        if(BlockExtractor.canExtract(target)) return set_type(BlockExtractor.extract(target))
        if(ItemExtractor.canExtract(target)) return set_type(ItemExtractor.extract(target))
        return set_type(EntityExtractor.extract(target))
    }

    override fun canExtract(target: Entity): Boolean {
        return  PotionExtractor.canExtract(target) ||
                BlockExtractor.canExtract(target) ||
                ItemExtractor.canExtract(target) ||
                EntityExtractor.canExtract(target)
    }

    override fun consume(target: Entity) {
        if(PotionExtractor.canExtract(target)) PotionExtractor.consume(target)
        else if(BlockExtractor.canExtract(target)) BlockExtractor.consume(target)
        else if(ItemExtractor.canExtract(target)) ItemExtractor.consume(target)
        else EntityExtractor.consume(target)
    }

    fun <T: Spirit>set_type(from: SpiritExtractor.ExtractionResult<T>): SpiritExtractor.ExtractionResult<Spirit>{
        return result(from.spirit, from.count)
    }

    override fun getExtractedName(): Text {
        return Text.translatable("hexlink.extractor.everything")
    }

    override fun getColor(): Int {
        val current_pi_time=(System.currentTimeMillis()%2000)/2000f*Math.PI*2
        val red=(sin(current_pi_time) *127+127).toInt()
        val blue=(sin(current_pi_time+Math.PI*2/3) *127+127).toInt()
        val green=(sin(current_pi_time+Math.PI*4/3) *127+127).toInt()
        return ColorHelper.Argb.getArgb(255, red, green, blue)
    }

    object Serializer : SpiritExtractorLoader<EntityExtractor> {
        override fun load(element: JsonElement): EntityExtractor {
            return EntityExtractor
        }
    }
}