package jempasam.hexlink.block

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.minecraft.client.render.RenderLayer

object HexlinkBlockClient {
    fun registerBlockRender(){
        BlockRenderLayerMap.INSTANCE.putBlock(HexlinkBlocks.VORTEX, RenderLayer.getCutout());
    }
}