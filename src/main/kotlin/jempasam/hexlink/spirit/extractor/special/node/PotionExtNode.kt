package jempasam.hexlink.spirit.extractor.special.node

import com.google.gson.JsonObject
import jempasam.hexlink.spirit.PotionSpirit
import jempasam.hexlink.spirit.StackHelper
import net.minecraft.item.Items
import net.minecraft.nbt.NbtInt
import net.minecraft.potion.PotionUtil
import net.minecraft.potion.Potions
import kotlin.math.max

object PotionExtNode : ExtractionNode{

    private val POTION_ITEMS=setOf(Items.POTION, Items.LINGERING_POTION, Items.SPLASH_POTION)
    override fun filter(source: ExtractionNode.Source): ExtractionNode.Source {
        val worldStack=StackHelper.stack(source.caster,source.entity)
        worldStack ?: return source
        val stack=worldStack.stack

        if(!POTION_ITEMS.contains(stack.item)) return source

        val effects= PotionUtil.getPotionEffects(stack)
        if(effects.isEmpty())return source

        return source.with {
            count=stack.count* max(stack.maxDamage,1)
            val prev=consumer
            consumer={
                prev(it)
                if (effects.size == 1) worldStack.killer()
                else {
                    if (PotionUtil.getPotion(stack) != Potions.EMPTY) {
                        stack.setCustomName(stack.name)
                        stack.orCreateNbt.put(PotionUtil.CUSTOM_POTION_COLOR_KEY, NbtInt.of(PotionUtil.getColor(stack)))
                        PotionUtil.setPotion(stack, Potions.EMPTY)
                    }
                    effects.removeAt(0)
                    PotionUtil.setCustomPotionEffects(stack, effects)
                    worldStack.update()
                }
            }
            spirit= PotionSpirit(effects[0].effectType)
        }
    }

    object Parser: ExtractionNode.Parser<PotionExtNode>{
        override fun parse(obj: JsonObject): PotionExtNode = PotionExtNode
    }
}