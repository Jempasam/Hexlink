package jempasam.hexlink.vortex

import jempasam.hexlink.spirit.Spirit

interface CatalyzedVortexHandler: HexVortexHandler {
    fun getCatalyzer(): List<Spirit>
}