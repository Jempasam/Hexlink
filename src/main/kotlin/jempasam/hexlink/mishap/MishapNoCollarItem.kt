package jempasam.hexlink.mishap

import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.GarbageIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

class MishapNoCollarItem : Mishap() {
    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Text {
        return Text.translatable("hexlink.mishap.no_collar_item")
    }

    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment = dyeColor(DyeColor.BROWN)

    override fun execute(ctx: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
        stack.add(GarbageIota())
    }
}