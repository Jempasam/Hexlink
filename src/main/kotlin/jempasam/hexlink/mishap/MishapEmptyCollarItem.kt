package jempasam.hexlink.mishap

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

class MishapEmptyCollarItem(val stack: ItemStack) : Mishap() {
    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Text {
        return stack.name.copy().append(Text.translatable("hexlink.mishap.empty_collar"))
    }

    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment = dyeColor(DyeColor.BROWN)

    override fun execute(ctx: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) { }
}