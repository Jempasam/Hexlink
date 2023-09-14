package jempasam.hexlink.data

import jempasam.hexlink.spirit.Spirit
import me.shedaniel.autoconfig.ConfigData

object HexlinkConfiguration : ConfigData {
    class SpiritSettings(val use_soul: Boolean, val media_cost: Int)
    val spirit_settings= mutableMapOf<Spirit.SpiritType<*>,SpiritSettings>()

}