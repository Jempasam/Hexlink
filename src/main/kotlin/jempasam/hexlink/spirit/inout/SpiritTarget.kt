package jempasam.hexlink.spirit.inout

import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.spirit.Spirit

interface SpiritTarget {
    fun fill(count: Int, spirit: Spirit): SpiritInputFlux

    class SpiritInputFlux(private val filler: ()->Unit, val count: Int){
        private var filled=false
        fun fill(){
            if(!filled)filler()
            HexlinkMod.logger.warn("Double filling, should not happen")
            filled=true
        }
    }

    object NONE: SpiritTarget{
        val FLUX= SpiritInputFlux({}, 0)
        override fun fill(count: Int, spirit: Spirit): SpiritInputFlux = FLUX
    }
}