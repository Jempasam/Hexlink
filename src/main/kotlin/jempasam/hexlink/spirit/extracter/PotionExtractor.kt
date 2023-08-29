package jempasam.hexlink.spirit.extracter

import jempasam.hexlink.spirit.PotionSpirit
import jempasam.hexlink.spirit.StackHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.nbt.NbtInt
import net.minecraft.potion.PotionUtil
import net.minecraft.potion.Potions
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import kotlin.math.max

object PotionExtractor : SpiritExtractor<PotionSpirit> {

    private val potion_items=setOf(Items.POTION, Items.LINGERING_POTION, Items.SPLASH_POTION)

    override fun extract(caster: PlayerEntity?, target: Entity): SpiritExtractor.ExtractionResult<PotionSpirit> {
        val worldstack= StackHelper.stack(caster, target)
        val stack= worldstack?.stack
        if(stack==null || !potion_items.contains(stack.item))return noResult()

        val effects= PotionUtil.getPotionEffects(stack)
        if(effects.isEmpty())return noResult()

        return SpiritExtractor.ExtractionResult(
                PotionSpirit(effects[0].effectType),
                max(1,effects[0].duration/1200) *(effects[0].amplifier+1)
        ) {
            if (effects.size == 1) worldstack.killer()
            else {
                if (PotionUtil.getPotion(stack) != Potions.EMPTY) {
                    stack.setCustomName(stack.name)
                    stack.orCreateNbt.put(PotionUtil.CUSTOM_POTION_COLOR_KEY, NbtInt.of(PotionUtil.getColor(stack)))
                    PotionUtil.setPotion(stack, Potions.EMPTY)
                }
                effects.removeAt(0)
                PotionUtil.setCustomPotionEffects(stack, effects)
                worldstack.update()
            }
        }
    }

    override fun getName(): Text = Text.translatable("hexlink.extractor.potion")

    override fun getColor(): Int = DyeColor.MAGENTA.fireworkColor

}