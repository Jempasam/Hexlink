package jempasam.hexlink.spirit.extractor.node

import com.google.gson.JsonObject
import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.spirit.Spirit
import net.minecraft.entity.Entity
import net.minecraft.server.network.ServerPlayerEntity

interface ExtractionNode {

    fun filter(source: Source): Source

    class Source(var count: Int, var caster: ServerPlayerEntity?, var entity: Entity, var spirit: Spirit?, var consumer: (Int)->Unit){
        private var consumed=false
        fun consume(count: Int){
            if(consumed)HexlinkMod.logger.error("Recipe source consumed twice")
            else{
                consumed=true
                consumer(count)
            }
        }

        fun copy(): Source = Source(count,caster,entity,spirit,consumer)

        inline fun with(definition: Source.()->Unit): Source {
            val ret=copy()
            definition(ret)
            return ret
        }
    }

    interface Parser<T: ExtractionNode>{
        fun parse(obj: JsonObject): T
    }
}