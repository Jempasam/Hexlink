package jempasam.hexlink.spirit.extractor.node

import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import jempasam.hexlink.spirit.ItemSpirit
import jempasam.hexlink.spirit.StackHelper
import net.minecraft.item.BlockItem
import net.minecraft.util.JsonHelper
import kotlin.math.ceil
import kotlin.math.max

class ItemExtNode(val useDurability: Boolean) : ExtractionNode {

    override fun filter(source: ExtractionNode.Source): ExtractionNode.Source {
        val worldStack=StackHelper.stack(source.caster,source.entity)
        worldStack ?: return source

        val stack=worldStack.stack
        val item=stack.item
        if(item is BlockItem) return source
        println("Trap: "+useDurability)
        return source.with {
            count *= stack.count * (if(useDurability) max(stack.maxDamage,1) else 1)
            val prev=consumer
            consumer={
                prev(it)
                val consumed=ceil(it.toFloat()/(if(useDurability) max(stack.maxDamage,1) else 1)).toInt()
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
        override fun parse(obj: JsonObject) = ItemExtNode(JsonHelper.getBoolean(obj,"useDurability",false))
    }

    val CODEC get() = Codec.unit(ItemExtNode(true))
}