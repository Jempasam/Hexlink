package jempasam.hexlink.spirit.extractor.special.node

import com.google.gson.JsonObject
import jempasam.hexlink.spirit.BiomeSpirit
import net.minecraft.util.math.BlockPos

object BiomeExtNode : ExtractionNode{
    override fun filter(source: ExtractionNode.Source): ExtractionNode.Source {
        val biome=source.entity.world.getBiome(BlockPos(source.entity.pos))
        return source.with {
            spirit=BiomeSpirit(biome)
        }
    }

    object Parser: ExtractionNode.Parser<BiomeExtNode>{
        override fun parse(obj: JsonObject): BiomeExtNode = BiomeExtNode
    }
}