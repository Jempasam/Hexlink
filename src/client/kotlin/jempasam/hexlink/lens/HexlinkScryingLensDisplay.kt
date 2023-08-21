package jempasam.hexlink.lens

import at.petrak.hexcasting.api.client.ScryingLensOverlayRegistry
import jempasam.hexlink.block.HexlinkBlocks

object HexlinkScryingLensDisplay {
    fun registerDisplays() {
        // Vortex Display
        ScryingLensOverlayRegistry.addDisplayer(HexlinkBlocks.VORTEX, VortexScryingLensDisplay())

        // Spirit Input Output
        /*for(block in listOf(HexlinkBlocks.VORTEX)){
            ScryingLensOverlayRegistry.addDisplayer(block, SpiritInOutLensDisplay())
        }*/
    }

}