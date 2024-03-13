package jempasam.hexlink.entity

import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.block.HexlinkBlocks
import jempasam.hexlink.entity.block.BigTabletBlockEntity
import jempasam.hexlink.entity.block.HexVortexBlockEntity
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object HexlinkEntities {

    fun <T: BlockEntity>register(id: String, type: BlockEntityType<T>): BlockEntityType<T>{
        Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier(HexlinkMod.MODID, id), type)
        return type
    }

    val HEX_VORTEX = register("hex_vortex",
            FabricBlockEntityTypeBuilder .create({bp,bs->HexVortexBlockEntity(bp,bs,60)}, HexlinkBlocks.VORTEX) .build()
    )
    val BIG_TABLET = register("big_tablet",
            FabricBlockEntityTypeBuilder .create({bp,bs->BigTabletBlockEntity(bp,bs,1000)}, HexlinkBlocks.BIG_TABLET) .build()
    )
}