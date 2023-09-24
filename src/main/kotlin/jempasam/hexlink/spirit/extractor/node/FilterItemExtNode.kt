package jempasam.hexlink.spirit.extractor.node

import com.google.gson.JsonObject
import jempasam.hexlink.spirit.StackHelper
import net.minecraft.predicate.item.ItemPredicate
import net.minecraft.util.JsonHelper

class FilterItemExtNode(val predicate: ItemPredicate, val not: Boolean) : ExtractionNode {
    override fun filter(source: ExtractionNode.Source): ExtractionNode.Source {
        val target=StackHelper.stack(source.caster,source.entity)
        return if(target!=null && predicate.test(target.stack) != not) source else source.with {count=0}
    }

    object Parser: ExtractionNode.Parser<FilterItemExtNode> {
        override fun parse(obj: JsonObject): FilterItemExtNode = FilterItemExtNode(
            ItemPredicate.fromJson(obj),
            JsonHelper.getBoolean(obj,"not", false)
        )
    }
}