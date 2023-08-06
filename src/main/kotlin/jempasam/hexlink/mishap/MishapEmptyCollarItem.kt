package jempasam.hexlink.mishap

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.Mishap
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

class MishapEmptyCollarItem(val stack: ItemStack) : Mishap() {
    override fun errorMessage(ctx: CastingContext, errorCtx: Context): Text {
        return stack.name.copy().append(Text.translatable("hexlink.mishap.empty_collar"))
    }

    override fun accentColor(ctx: CastingContext, errorCtx: Context): FrozenColorizer = dyeColor(DyeColor.BROWN)

    override fun execute(ctx: CastingContext, errorCtx: Context, stack: MutableList<Iota>) { }
}