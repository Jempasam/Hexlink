package jempasam.hexlink.mishap

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.Mishap
import jempasam.hexlink.spirit.Spirit
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

class MishapNoEnoughSoul(val spirit: Spirit, val missing: Int): Mishap() {
    override fun errorMessage(ctx: CastingContext, errorCtx: Context): Text {
        return Text.translatable("hexlink.mishap.not_enough_soul", missing)
    }

    override fun accentColor(ctx: CastingContext, errorCtx: Context): FrozenColorizer = dyeColor(DyeColor.CYAN)

    override fun execute(ctx: CastingContext, errorCtx: Context, stack: MutableList<Iota>) {
    }
}