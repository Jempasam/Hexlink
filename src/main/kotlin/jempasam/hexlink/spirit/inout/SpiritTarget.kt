package jempasam.hexlink.spirit.inout

import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.spirit.Spirit

interface SpiritTarget {
    fun fill(count: Int, spirit: Spirit): SpiritInputFlux

    class SpiritInputFlux(private val filler: (Int)->Unit, val maxcount: Int){
        private var filled=false
        fun fill(count: Int){
            var finalCount=count
            if(finalCount>maxcount){
                finalCount=maxcount
                HexlinkMod.logger.warn("Try to fill more than max, should not happen")
            }

            if(!filled) {
                if (finalCount > 0) {
                    filler(finalCount)
                    filled=true
                }
            }
            else HexlinkMod.logger.warn("Double filling, should not happen")
        }
    }

    object NONE: SpiritTarget{
        val FLUX= SpiritInputFlux({}, 0)
        override fun fill(count: Int, spirit: Spirit): SpiritInputFlux = FLUX
    }
}