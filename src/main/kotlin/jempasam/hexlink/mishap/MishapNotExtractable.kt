package jempasam.hexlink.mishap

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.Mishap
import jempasam.hexlink.spirit.extractor.SpiritExtractor
import net.minecraft.entity.Entity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

class MishapNotExtractable(val entity: Entity, val extractor: SpiritExtractor<*>) : Mishap() {
    override fun errorMessage(ctx: CastingContext, errorCtx: Context): Text {
        return Text.translatable("hexlink.mishap.not_extractable", entity.name, extractor.getName()).setStyle(Style.EMPTY.withColor(DyeColor.RED.signColor))
        //return entity.name.copy().append(Text.translatable("hexlink.mishap.not_extractable")).append(item.name).setStyle(Style.EMPTY.withColor(DyeColor.RED.signColor))
    }

    override fun accentColor(ctx: CastingContext, errorCtx: Context): FrozenColorizer {
        return dyeColor(DyeColor.RED)
    }

    override fun execute(ctx: CastingContext, errorCtx: Context, stack: MutableList<Iota>) {
        ctx.caster.addStatusEffect(StatusEffectInstance(StatusEffects.NAUSEA,120))
    }

}