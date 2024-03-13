package jempasam.hexlink.mishap

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import jempasam.hexlink.spirit.Spirit
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

class MishapNoEnoughSoul(val spirit: Spirit, val missing: Int): Mishap() {
    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Text {
        return Text.translatable("hexlink.mishap.not_enough_soul", missing)
    }

    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment = dyeColor(DyeColor.CYAN)

    override fun execute(ctx: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
    }
}