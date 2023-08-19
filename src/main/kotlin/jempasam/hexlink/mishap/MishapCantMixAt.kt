package jempasam.hexlink.mishap

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.Mishap
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

class MishapCantMixAt : Mishap() {
    override fun errorMessage(ctx: CastingContext, errorCtx: Context): Text
        = Text.translatable("hexlink.mishap.cant_mix_at")

    override fun accentColor(ctx: CastingContext, errorCtx: Context): FrozenColorizer = dyeColor(DyeColor.MAGENTA)

    override fun execute(ctx: CastingContext, errorCtx: Context, stack: MutableList<Iota>) {
    }
}