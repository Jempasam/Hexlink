package jempasam.hexlink.spirit.extractor.special.node

import com.google.gson.JsonObject

object ElseExtNode : ExtractionNode{
    override fun filter(source: ExtractionNode.Source): ExtractionNode.Source {
        return source.with {
            if(count==0)spirit=null
            count=1
        }
    }

    object Parser: ExtractionNode.Parser<ElseExtNode>{
        override fun parse(obj: JsonObject): ElseExtNode = ElseExtNode
    }
}