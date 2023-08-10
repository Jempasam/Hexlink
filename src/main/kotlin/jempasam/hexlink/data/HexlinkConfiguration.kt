package jempasam.hexlink.data

import jempasam.hexlink.spirit.extracter.SpiritExtractor
import me.shedaniel.autoconfig.ConfigData

object HexlinkConfiguration : ConfigData {
    class ExtractorSettings(val success_rate: Float, val soul_count: Int, val use_soul: Boolean)
    val extractor_settings= mutableMapOf<SpiritExtractor<*>, ExtractorSettings>()

}