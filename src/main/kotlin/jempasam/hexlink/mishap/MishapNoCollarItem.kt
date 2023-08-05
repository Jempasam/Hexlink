package jempasam.hexlink.mishap

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.Mishap
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

class MishapNoCollarItem : Mishap() {
    override fun errorMessage(ctx: CastingContext, errorCtx: Context): Text {
        return Text.translatable("hexlink.mishap.no_collar_item")
    }

    override fun accentColor(ctx: CastingContext, errorCtx: Context): FrozenColorizer = dyeColor(DyeColor.BROWN)

    override fun execute(ctx: CastingContext, errorCtx: Context, stack: MutableList<Iota>) {
    }
}