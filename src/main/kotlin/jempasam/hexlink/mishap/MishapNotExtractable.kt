package jempasam.hexlink.mishap

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import jempasam.hexlink.spirit.extractor.SpiritExtractor
import net.minecraft.entity.Entity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

class MishapNotExtractable(val entity: Entity, val extractor: SpiritExtractor<*>) : Mishap() {
    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Text {
        return Text.translatable("hexlink.mishap.not_extractable", entity.name, extractor.getName()).setStyle(Style.EMPTY.withColor(DyeColor.RED.signColor))
        //return entity.name.copy().append(Text.translatable("hexlink.mishap.not_extractable")).append(item.name).setStyle(Style.EMPTY.withColor(DyeColor.RED.signColor))
    }

    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment {
        return dyeColor(DyeColor.RED)
    }

    override fun execute(ctx: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {

        ctx.castingEntity?.addStatusEffect(StatusEffectInstance(StatusEffects.NAUSEA,120))
    }

}