package jempasam.hexlink.data

import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.extractor.SpiritExtractor
import me.shedaniel.autoconfig.ConfigData

object HexlinkConfiguration : ConfigData {
    class ExtractorSettings(val soulCount: Int, val extraction_media_cost: Int)
    val extractor_settings= mutableMapOf<SpiritExtractor<*>, ExtractorSettings>()

    class SpiritSettings(val use_soul: Boolean, val media_cost: Int)
    val spirit_settings= mutableMapOf<Spirit.SpiritType<*>,SpiritSettings>()

}