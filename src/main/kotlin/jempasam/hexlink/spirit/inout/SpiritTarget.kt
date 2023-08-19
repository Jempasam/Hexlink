package jempasam.hexlink.spirit.inout

import jempasam.hexlink.spirit.Spirit

interface SpiritTarget {
    fun fill(count: Int, spirit: Spirit): SpiritInputFlux?

    class SpiritInputFlux(private val filler: ()->Unit, val count: Int){
        fun fill() = filler()
    }

    object NONE: SpiritTarget{
        override fun fill(count: Int, spirit: Spirit): SpiritInputFlux? = null
    }
}