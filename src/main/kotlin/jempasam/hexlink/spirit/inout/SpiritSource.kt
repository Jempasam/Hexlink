package jempasam.hexlink.spirit.inout

import jempasam.hexlink.spirit.Spirit

interface SpiritSource {
    fun extract(count: Int, spirit: Spirit): SpiritOutputFlux?

    class SpiritOutputFlux(private val consumer: ()->Unit, val count: Int){
        fun consume() = consumer()
    }

    object NONE: SpiritSource{
        override fun extract(count: Int, spirit: Spirit): SpiritOutputFlux? = null
    }
}