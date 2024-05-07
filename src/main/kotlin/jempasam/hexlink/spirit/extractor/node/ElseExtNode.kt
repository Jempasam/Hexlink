package jempasam.hexlink.spirit.extractor.node

import com.google.gson.JsonObject
import com.mojang.serialization.Codec

object ElseExtNode : ExtractionNode {
    override fun filter(source: ExtractionNode.Source): ExtractionNode.Source {
        return source.with {
            if(count==0)spirit=null
            count=1
        }
    }

    object Parser: ExtractionNode.Parser<ElseExtNode> {
        override fun parse(obj: JsonObject): ElseExtNode = ElseExtNode
    }

    val CODEC= Codec.unit(ElseExtNode)
}