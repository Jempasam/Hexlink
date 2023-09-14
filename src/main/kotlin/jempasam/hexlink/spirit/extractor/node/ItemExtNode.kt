package jempasam.hexlink.spirit.extractor.node

import com.google.gson.JsonObject
import jempasam.hexlink.spirit.ItemSpirit
import jempasam.hexlink.spirit.StackHelper
import net.minecraft.item.BlockItem
import kotlin.math.ceil
import kotlin.math.max

object ItemExtNode : ExtractionNode {

    override fun filter(source: ExtractionNode.Source): ExtractionNode.Source {
        val worldStack=StackHelper.stack(source.caster,source.entity)
        worldStack ?: return source

        val stack=worldStack.stack
        val item=stack.item
        if(item is BlockItem) return source

        return source.with {
            count *= stack.count*max(stack.maxDamage,1)
            val prev=consumer
            consumer={
                prev(it)
                val consumed=ceil(it as Float/max(stack.maxDamage,1)) as Int
                if(it>=worldStack.stack.count) worldStack.killer()
                else{
                    stack.count-=consumed
                    worldStack.update()
                }
            }
            spirit=ItemSpirit(stack.item)
        }
    }

    object Parser: ExtractionNode.Parser<ItemExtNode> {
        override fun parse(obj: JsonObject): ItemExtNode = ItemExtNode
    }
}