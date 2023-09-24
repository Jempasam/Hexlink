package jempasam.hexlink.spirit.extractor.node

import com.google.gson.JsonObject
import net.minecraft.predicate.entity.EntityPredicate
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.JsonHelper

class FilterEntityExtNode(val predicate: EntityPredicate, val not: Boolean) : ExtractionNode {
    override fun filter(source: ExtractionNode.Source): ExtractionNode.Source {
        val success = if(source.caster!=null){
            predicate.test(source.caster,source.entity) != not
        } else{
            predicate.test(source.entity.world as ServerWorld, source.entity.pos,source.entity) != not
        }
        return if(success) source else source.with{count=0}
    }

    object Parser: ExtractionNode.Parser<FilterEntityExtNode> {
        override fun parse(obj: JsonObject): FilterEntityExtNode = FilterEntityExtNode(
            EntityPredicate.fromJson(obj),
            JsonHelper.getBoolean(obj,"not", false)
        )
    }
}