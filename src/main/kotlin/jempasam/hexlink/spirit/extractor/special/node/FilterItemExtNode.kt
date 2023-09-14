package jempasam.hexlink.spirit.extractor.special.node

import com.google.gson.JsonObject
import jempasam.hexlink.spirit.StackHelper
import net.minecraft.predicate.item.ItemPredicate

class FilterItemExtNode(val predicate: ItemPredicate) : ExtractionNode{
    override fun filter(source: ExtractionNode.Source): ExtractionNode.Source {
        val target=StackHelper.stack(source.caster,source.entity)
        if(target!=null && predicate.test(target.stack)) print("AO SUCCESS")
        else print("AO FAIL")
        return if(target!=null && predicate.test(target.stack)) source else source.with {count=0}
    }

    object Parser: ExtractionNode.Parser<FilterItemExtNode>{
        override fun parse(obj: JsonObject): FilterItemExtNode = FilterItemExtNode(ItemPredicate.fromJson(obj))
    }
}