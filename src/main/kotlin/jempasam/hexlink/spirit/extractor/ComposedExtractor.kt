package jempasam.hexlink.spirit.extractor

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import jempasam.hexlink.HexlinkRegistry
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.extractor.special.node.ExtractionNode
import jempasam.hexlink.spirit.extractor.special.node.ExtractionNode.Source
import jempasam.hexlink.utils.read
import net.minecraft.entity.Entity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.math.ColorHelper.Argb.*

class ComposedExtractor(private val name: Text, private val colors: List<Int>, private val duration: Int, private val root: PlacedNode): SpiritExtractor<Spirit> {

    override fun extract(caster: ServerPlayerEntity?, target: Entity): SpiritExtractor.ExtractionResult<Spirit> {
        val ret=root.filter(Source(1, caster, target, null) {})
        return SpiritExtractor.ExtractionResult(ret.spirit, ret.count, ret::consume)
    }

    override fun getColor(): Int{
        if(colors.isEmpty())return 0x000000
        else if(colors.size==1)return colors[0]
        else{
            val time=(System.currentTimeMillis()%(colors.size*duration-1)).toInt()
            val actual=time/duration
            val previous= if(actual==0) colors.size-1 else actual-1
            val transition=time%duration/duration.toFloat()
            val rtransition=1f-transition

            val pcolor=colors[previous]
            val acolor=colors[actual]
            return getArgb(
                255,
                (getRed(pcolor) *rtransition + getRed(acolor) *transition).toInt(),
                (getGreen(pcolor) *rtransition + getGreen(acolor) *transition).toInt(),
                (getBlue(pcolor) *rtransition + getBlue(acolor) *transition).toInt()
            )
        }
    }

    override fun getName(): Text = name

    interface PlacedNode{
        fun filter(source: Source): Source
    }
    private class ListPlacedNode(val nodes: List<PlacedNode>): PlacedNode{
        override fun filter(source: Source): Source{
            var ret=source
            for(n in nodes){
                if(ret.count<=0)break
                ret=n.filter(ret)
            }
            return ret
        }
    }

    private class UniquePlacedNode(val node: ExtractionNode): PlacedNode{
        override fun filter(source: Source): Source = node.filter(source)
    }

    private class MultiPlacedNode(val content: List<PlacedNode>): PlacedNode{
        override fun filter(source: Source): Source{
            for(c in content){
                val ret=c.filter(source.copy())
                if(ret.count!=0 && ret.spirit!=null)return ret
            }
            return source.with { count=0 }
        }
    }

    companion object{
        fun parse(obj: JsonObject): ComposedExtractor {
            return ComposedExtractor(
                obj.get("name")?.let { Text.Serializer.fromJson(it) } ?: Text.of("INVALIDNAME"),
                obj.get("color")?.asJsonArray?.read { it.asInt } ?: listOf(0x000000),
                JsonHelper.getInt(obj,"color_duration",1000),
                parseAny(JsonHelper.getArray(obj, "nodes"))
            )
        }

        private fun parseAny(element: JsonElement): PlacedNode{
            return if(element.isJsonArray){
                val array=element.asJsonArray
                if(array.size()>0 && array[0].isJsonPrimitive && array[0].asString=="ANY"){
                    parseMulti(element.asJsonArray)
                }
                else parseList(array)
            }
            else parseUnique(element)
        }

        private fun parseList(list: JsonArray): PlacedNode{
            val nodes= list.read(::parseAny)
            return ListPlacedNode(nodes)
        }

        private fun parseMulti(element: JsonArray): PlacedNode{
            element.remove(0)
            val ret= element.read(::parseAny)
            return MultiPlacedNode(ret)
        }

        private fun parseUnique(element: JsonElement): UniquePlacedNode{
            val (id,obj)=when{
                element.isJsonPrimitive && element.asJsonPrimitive.isString
                    -> element.asString to JsonObject()
                element.isJsonObject
                    -> JsonHelper.getString(element.asJsonObject,"node") to element.asJsonObject
                else
                    -> throw JsonParseException("Middle node should be an object of a string")
            }
            val parser=HexlinkRegistry.EXTRACTOR_NODE_PARSER.get(Identifier(id))
                ?: throw JsonParseException("\"${element.asString}\" is not a valid extraction node type")
            return UniquePlacedNode(parser.parse(obj))
        }

    }
}