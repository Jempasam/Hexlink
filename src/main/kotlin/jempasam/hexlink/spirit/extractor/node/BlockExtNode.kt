package jempasam.hexlink.spirit.extractor.node

import com.google.gson.JsonObject
import jempasam.hexlink.spirit.BlockSpirit
import jempasam.hexlink.spirit.StackHelper
import net.minecraft.item.BlockItem
import kotlin.math.ceil
import kotlin.math.max

object BlockExtNode : ExtractionNode {
    override fun filter(source: ExtractionNode.Source): ExtractionNode.Source {
        val worldStack=StackHelper.stack(source.caster,source.entity)
        worldStack ?: return source

        val stack=worldStack.stack
        val item=stack.item
        if(item !is BlockItem) return source

        return source.with {
            count *= stack.count*max(worldStack.stack.maxDamage,1)
            val prev=consumer
            consumer={
                prev(it)
                val consumed=ceil(it.toFloat()/max(stack.maxDamage,1)).toInt()
                if(it>=stack.count) worldStack.killer()
                else{
                    stack.count-=consumed
                    worldStack.update()
                }
            }
            spirit=BlockSpirit(item.block)
        }
    }

    object Parser: ExtractionNode.Parser<BlockExtNode> {
        override fun parse(obj: JsonObject): BlockExtNode = BlockExtNode
    }
}