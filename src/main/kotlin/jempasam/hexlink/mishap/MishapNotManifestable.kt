package jempasam.hexlink.mishap

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.GarbageIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.Mishap
import jempasam.hexlink.spirit.Spirit
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

class MishapNotManifestable(val spirit: Spirit, val target: Any) : Mishap() {
    override fun accentColor(ctx: CastingContext, errorCtx: Context): FrozenColorizer = dyeColor(DyeColor.BLUE)

    override fun errorMessage(ctx: CastingContext, errorCtx: Context): Text
        = Text.translatable("hexlink.mishap.not_manifestable", spirit.getName(), target.toString())

    override fun execute(ctx: CastingContext, errorCtx: Context, stack: MutableList<Iota>) {
        stack[stack.size-1]=GarbageIota()
    }
}