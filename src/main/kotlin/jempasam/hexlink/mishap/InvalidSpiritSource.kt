package jempasam.hexlink.mishap

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

class InvalidSpiritSource(val iota: Iota) : Mishap() {
    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Text {
        return Text.translatable("hexlink.mishap.invalid_spirit_source", iota.type.display(iota.serialize()))
    }

    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment {
        return dyeColor(DyeColor.PINK)
    }

    override fun execute(ctx: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
        ctx.castingEntity?.addStatusEffect(StatusEffectInstance(StatusEffects.BLINDNESS,40))
    }
}