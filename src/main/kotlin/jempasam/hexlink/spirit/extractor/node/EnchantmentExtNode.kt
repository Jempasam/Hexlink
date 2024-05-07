package jempasam.hexlink.spirit.extractor.node

import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import jempasam.hexlink.spirit.EnchantmentSpirit
import jempasam.hexlink.spirit.StackHelper
import jempasam.hexlink.utils.EnchantHelper
import net.minecraft.enchantment.EnchantmentHelper

object EnchantmentExtNode : ExtractionNode {

    override fun filter(source: ExtractionNode.Source): ExtractionNode.Source {
        val worldStack= StackHelper.stack(source.caster,source.entity) ?: return source
        val enchantments= EnchantmentHelper.get(worldStack.stack)
        if(enchantments.isEmpty())return source
        val extracted=enchantments.entries.first()
        return source.with {
            count *= 1 shl (extracted.value-1)
            spirit=EnchantmentSpirit(extracted.key)
            val prev=consumer
            consumer={
                prev(it)
                val nlevel=extracted.value-it.countTrailingZeroBits()-1
                worldStack.replace(
                    if(nlevel==0){
                        EnchantHelper.removeEnchantment(worldStack.stack, extracted.key)
                    }
                    else{
                        EnchantHelper.removeEnchantment(worldStack.stack, extracted.key)
                            .let { EnchantHelper.enchant(it, extracted.key, nlevel) ?: it }
                    }
                )
            }
        }
    }

    object Parser: ExtractionNode.Parser<EnchantmentExtNode> {
        override fun parse(obj: JsonObject): EnchantmentExtNode = EnchantmentExtNode
    }

    val CODEC= Codec.unit(EnchantmentExtNode)
}