package jempasam.hexlink.spirit.extractor

import com.google.gson.JsonElement
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.extractor.loaders.SpiritExtractorLoader
import net.minecraft.entity.Entity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.math.ColorHelper
import kotlin.math.sin

object EverythingExtractor : SpiritExtractor<Spirit> {

    val EXTRACTORS= listOf(PotionExtractor, BlockExtractor, ItemExtractor, EntityExtractor)
    override fun extract(caster: ServerPlayerEntity?, target: Entity): SpiritExtractor.ExtractionResult<Spirit> {
        for(e in EXTRACTORS){
            val ret=e.extract(caster,target)
            if(ret.spirit!=null)return ret.downCast()
        }
        return noResult()
    }

    override fun getName(): Text = Text.translatable("hexlink.extractor.everything")

    override fun getColor(): Int {
        val currentPiTime=(System.currentTimeMillis()%2000)/2000f*Math.PI*2
        val red=(sin(currentPiTime) *127+127).toInt()
        val blue=(sin(currentPiTime+Math.PI*2/3) *127+127).toInt()
        val green=(sin(currentPiTime+Math.PI*4/3) *127+127).toInt()
        return ColorHelper.Argb.getArgb(255, red, green, blue)
    }

    object Serializer : SpiritExtractorLoader<EntityExtractor> {
        override fun load(element: JsonElement): EntityExtractor {
            return EntityExtractor
        }
    }
}