package jempasam.hexlink.spirit.extracter.loaders

import com.google.gson.JsonElement
import jempasam.hexlink.spirit.extracter.SpiritExtractor

interface SpiritExtractorLoader<T: SpiritExtractor<*>> {
    fun load(element: JsonElement): T
}