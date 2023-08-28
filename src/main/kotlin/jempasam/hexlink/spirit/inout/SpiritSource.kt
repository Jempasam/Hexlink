package jempasam.hexlink.spirit.inout

import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.spirit.Spirit

interface SpiritSource {
    fun extract(count: Int, spirit: Spirit): SpiritOutputFlux

    fun last(): Spirit?

    class SpiritOutputFlux(private val consumer: (Int)->Unit, val maxcount: Int){
        private var consumed=false
        fun consume(count: Int){
            var finalCount=count
            if(finalCount>maxcount){
                finalCount=maxcount
                HexlinkMod.logger.warn("Try to consume more than max, should not happen")
            }
            if(!consumed){
                if(finalCount>0){
                    consumer(finalCount)
                    consumed=true
                }
            }
            else HexlinkMod.logger.warn("Double consuption, should not happen")

        }
    }

    object NONE: SpiritSource{
        val FLUX=SpiritOutputFlux({},0)
        override fun extract(count: Int, spirit: Spirit): SpiritOutputFlux = FLUX

        override fun last(): Spirit? = null
    }
}