package jempasam.hexlink.spirit.extractor.node

import com.google.gson.JsonObject
import jempasam.hexlink.HexlinkRegistry
import jempasam.hexlink.spirit.extractor.NodeExtractor
import net.minecraft.util.Identifier
import net.minecraft.util.registry.RegistryKey

/**
 * A spirit extractor node referencing another spirit extractor
 */
class ReferenceExtNode(val referenced: RegistryKey<NodeExtractor>) : ExtractionNode {

    override fun filter(source: ExtractionNode.Source): ExtractionNode.Source {
        val extractor= HexlinkRegistry.EXTRACTOR.get(referenced)
        return extractor?.filter(source) ?: source
    }

    object Parser: ExtractionNode.Parser<ReferenceExtNode> {
        override fun parse(obj: JsonObject): ReferenceExtNode{
            val id= obj.get("id").asString
            val key= RegistryKey.of(HexlinkRegistry.EXTRACTOR_KEY, Identifier(id))
            return ReferenceExtNode(key)
        }
    }

    val CODEC= RegistryKey.createCodec(HexlinkRegistry.EXTRACTOR_KEY)
}