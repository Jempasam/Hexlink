package jempasam.hexlink.spirit.extractor.node

import com.google.gson.JsonObject
import net.minecraft.util.JsonHelper
import kotlin.math.ceil

class CostExtNode(val base: Int, val multiplier: Float) : ExtractionNode {
    override fun filter(source: ExtractionNode.Source): ExtractionNode.Source {
        return source.with {
            val prev=consumer
            consumer={prev(ceil(it*multiplier+base).toInt())}
        }
    }

    object Parser: ExtractionNode.Parser<CostExtNode> {
        override fun parse(obj: JsonObject): CostExtNode {
            return CostExtNode(
                JsonHelper.getInt(obj,"base", 0),
                JsonHelper.getFloat(obj,"multiplier", 1.0f)
            )
        }
    }
}