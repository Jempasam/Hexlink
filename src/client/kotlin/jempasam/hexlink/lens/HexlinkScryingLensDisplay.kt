package jempasam.hexlink.lens

import at.petrak.hexcasting.api.client.ScryingLensOverlayRegistry
import jempasam.hexlink.block.HexlinkBlocks

object HexlinkScryingLensDisplay {
    fun registerDisplays() {
        // Vortex Display
        ScryingLensOverlayRegistry.addDisplayer(HexlinkBlocks.VORTEX, VortexScryingLensDisplay())
    }

}