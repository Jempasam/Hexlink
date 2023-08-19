package jempasam.hexlink.block

import jempasam.hexlink.HexlinkMod
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.Material
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object HexlinkBlocks {

    class BlockEntry(val id: String, val block: Block)
    private val blocks= mutableListOf<BlockEntry>()

    private fun <T: Block>create(id: String, block: T): T{
        blocks.add(BlockEntry(id,block))
        return block
    }

    fun registerBlocks(){
        for(entry in blocks)
            Registry.register(Registry.BLOCK, Identifier(HexlinkMod.MODID, entry.id), entry.block)
    }


    val VORTEX= create( "vortex", HexVortexBlock(AbstractBlock.Settings.of(Material.AMETHYST).nonOpaque().emissiveLighting{_,_,_->true}.luminance{_->6}) )
}