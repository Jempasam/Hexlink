package jempasam.hexlink.block

import jempasam.hexlink.block.color.SpiritContainerBlockColor
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.client.render.RenderLayer

object HexlinkBlockClient {
    fun registerBlockRender(){
        BlockRenderLayerMap.INSTANCE.putBlock(HexlinkBlocks.VORTEX, RenderLayer.getCutout())
        BlockRenderLayerMap.INSTANCE.putBlock(HexlinkBlocks.BIG_TABLET, RenderLayer.getCutout())
    }

    fun registerBlockColors(){
        ColorProviderRegistry.BLOCK.register(SpiritContainerBlockColor(),HexlinkBlocks.BIG_TABLET)
    }
}