package jempasam.hexlink.mishap

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.Mishap
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

class InvalidSpiritSource(val iota: Iota) : Mishap() {
    override fun errorMessage(ctx: CastingContext, errorCtx: Context): Text {
        return Text.translatable("hexlink.mishap.invalid_spirit_source", iota.type.display(iota.serialize()))
    }

    override fun accentColor(ctx: CastingContext, errorCtx: Context): FrozenColorizer {
        return dyeColor(DyeColor.PINK)
    }

    override fun execute(ctx: CastingContext, errorCtx: Context, stack: MutableList<Iota>) {
        ctx.caster.addStatusEffect(StatusEffectInstance(StatusEffects.BLINDNESS,40))
    }
}