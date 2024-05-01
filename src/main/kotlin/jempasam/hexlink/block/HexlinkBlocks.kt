package jempasam.hexlink.block

import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.entity.HexlinkEntities
import jempasam.hexlink.entity.block.BigTabletBlockEntity
import jempasam.hexlink.entity.block.HexVortexBlockEntity
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.Material
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.Identifier
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.Registry
import net.minecraft.util.shape.VoxelShapes

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


    val VORTEX: SpiritContainerBlock<HexVortexBlockEntity> = create( "vortex", SpiritContainerBlock(
        AbstractBlock.Settings.of(Material.AMETHYST).nonOpaque().emissiveLighting{_,_,_->true}.luminance{_->6}.sounds(BlockSoundGroup.AMETHYST_BLOCK),
        {HexlinkEntities.HEX_VORTEX},
        { w, bp, bs, t -> t.tick(w,bp,bs) },
        2,
        VoxelShapes.cuboid(Box.of(Vec3d(.5,.5,.5), 0.8, 1.0, 0.8))
    ))

    val BIG_TABLET: SpiritContainerBlock<BigTabletBlockEntity> = create( "big_tablet", FacingSpiritContainerBlock(
        AbstractBlock.Settings.of(Material.STONE).nonOpaque().sounds(BlockSoundGroup.STONE),
        {HexlinkEntities.BIG_TABLET},
        { w, bp, bs, t -> t.tick(w,bp,bs) },
        1,
        VoxelShapes.cuboid(Box.of(Vec3d(.5,.5,.5), 0.75, 1.0, 0.25))
    ))
}