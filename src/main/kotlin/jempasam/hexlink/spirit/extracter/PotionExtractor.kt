package jempasam.hexlink.spirit.extracter

import jempasam.hexlink.spirit.PotionSpirit
import jempasam.hexlink.spirit.StackHelper
import net.minecraft.entity.Entity
import net.minecraft.item.Items
import net.minecraft.nbt.NbtInt
import net.minecraft.potion.PotionUtil
import net.minecraft.potion.Potions
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

object PotionExtractor : SpiritExtractor<PotionSpirit> {
    private val potion_items=setOf(Items.POTION, Items.LINGERING_POTION, Items.SPLASH_POTION)

    override fun canExtract(target: Entity): Boolean {
        val stack= StackHelper.stack(null, target)?.stack
        if(stack!=null && potion_items.contains(stack.item)){
            val effects= PotionUtil.getPotionEffects(stack)
            return effects.isNotEmpty()
        }
        return false
    }

    override fun extract(target: Entity): SpiritExtractor.ExtractionResult<PotionSpirit> {
        val stack= StackHelper.stackOrThrow(null, target).stack
        val effect= PotionUtil.getPotionEffects(stack).get(0)
        println("DURATION:" + effect.duration)
        return result(PotionSpirit(effect.effectType), Math.max(1,effect.duration/1200)*(effect.amplifier+1))
    }

    override fun consume(target: Entity) {
        val worldstack= StackHelper.stackOrThrow(null, target)
        val stack= worldstack.stack
        val effects = PotionUtil.getPotionEffects(stack)
        if (effects.size == 1) worldstack.killer()
        else {
            if (PotionUtil.getPotion(stack) != Potions.EMPTY) {
                stack.setCustomName(stack.name)
                stack.orCreateNbt.put(PotionUtil.CUSTOM_POTION_COLOR_KEY, NbtInt.of(PotionUtil.getColor(stack)))
                PotionUtil.setPotion(stack, Potions.EMPTY)
            }
            effects.removeAt(0)
            PotionUtil.setCustomPotionEffects(stack, effects)
        }
    }

    override fun getExtractedName(): Text {
        return Text.translatable("hexlink.extractor.potion")
    }

    override fun getColor(): Int {
        return DyeColor.MAGENTA.fireworkColor
    }
}