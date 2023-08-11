package jempasam.hexlink.data

import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.extracter.SpiritExtractor
import me.shedaniel.autoconfig.ConfigData

object HexlinkConfiguration : ConfigData {
    class ExtractorSettings(val success_rate: Float, val soul_count: Int, val extraction_media_cost: Int)
    val extractor_settings= mutableMapOf<SpiritExtractor<*>, ExtractorSettings>()

    class SpiritSettings(val use_soul: Boolean, val media_cost: Int)
    val spirit_settings= mutableMapOf<Spirit.SpiritType<*>,SpiritSettings>()

}