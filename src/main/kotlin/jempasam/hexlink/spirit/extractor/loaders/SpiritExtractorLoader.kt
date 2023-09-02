package jempasam.hexlink.spirit.extractor.loaders

import com.google.gson.JsonElement
import jempasam.hexlink.spirit.extractor.SpiritExtractor

interface SpiritExtractorLoader<T: SpiritExtractor<*>> {
    fun load(element: JsonElement): T
}