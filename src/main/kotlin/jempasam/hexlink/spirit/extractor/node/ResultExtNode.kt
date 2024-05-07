package jempasam.hexlink.spirit.extractor.node

import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.utils.NbtHelper
import jempasam.hexlink.utils.asNBT
import net.minecraft.util.JsonHelper
import net.minecraft.util.dynamic.Codecs
import kotlin.math.ceil

class ResultExtNode(val base: Int, val multiplier: Float, val newSpirit: Spirit?) : ExtractionNode {
    override fun filter(source: ExtractionNode.Source): ExtractionNode.Source {
        return source.with {
            count= base+(multiplier*count).toInt()
            val prev=consumer
            consumer={prev(ceil((it-base)/ multiplier).toInt())}
            spirit= newSpirit ?: spirit
        }
    }

    object Parser: ExtractionNode.Parser<ResultExtNode> {
        override fun parse(obj: JsonObject): ResultExtNode {
            return ResultExtNode(
                JsonHelper.getInt(obj,"base", 0),
                JsonHelper.getFloat(obj,"multiplier", 1.0f),
                obj.get("spirit") ?.asJsonObject ?.let { NbtHelper.readSpirit(it.asNBT()) }
            )
        }
    }

    companion object{
        val CODEC= RecordCodecBuilder.create<CostExtNode> { inst ->
            inst.group(
                Codecs.NONNEGATIVE_INT .optionalFieldOf("base",0) .forGetter(CostExtNode::base),
                Codec.FLOAT .optionalFieldOf("multiplier",1.0f) .forGetter(CostExtNode::multiplier),

            ).apply(inst, ::CostExtNode)
        }
    }
}