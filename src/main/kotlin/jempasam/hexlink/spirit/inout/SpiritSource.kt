package jempasam.hexlink.spirit.inout

import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.spirit.Spirit

interface SpiritSource {
    fun extract(count: Int, spirit: Spirit): SpiritOutputFlux

    class SpiritOutputFlux(private val consumer: ()->Unit, val count: Int){
        private var consumed=false
        fun consume(){
            if(!consumed)consumer()
            HexlinkMod.logger.warn("Double consuption, should not happen")
            consumed=true
        }
    }

    object NONE: SpiritSource{
        val FLUX=SpiritOutputFlux({},0)
        override fun extract(count: Int, spirit: Spirit): SpiritOutputFlux = FLUX
    }
}