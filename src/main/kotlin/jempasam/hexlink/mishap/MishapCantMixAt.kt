package jempasam.hexlink.mishap

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

class MishapCantMixAt : Mishap() {
    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Text
        = Text.translatable("hexlink.mishap.cant_mix_at")

    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment = dyeColor(DyeColor.MAGENTA)

    override fun execute(ctx: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
    }
}