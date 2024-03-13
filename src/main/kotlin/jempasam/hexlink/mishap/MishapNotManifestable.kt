package jempasam.hexlink.mishap

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.GarbageIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import jempasam.hexlink.spirit.Spirit
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

class MishapNotManifestable(val spirit: Spirit, val target: Iota) : Mishap() {
    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment = dyeColor(DyeColor.BLUE)

    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Text
        = Text.translatable("hexlink.mishap.not_manifestable", spirit.getName(), target.type.display(target.serialize()))

    override fun execute(ctx: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
        stack[stack.size-1]=GarbageIota()
    }
}