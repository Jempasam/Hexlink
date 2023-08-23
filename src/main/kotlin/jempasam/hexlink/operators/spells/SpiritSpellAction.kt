package jempasam.hexlink.operators.spells

import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import jempasam.hexlink.operators.getSpiritSource
import jempasam.hexlink.spirit.inout.SpiritHelper
import jempasam.hexlink.spirit.inout.SpiritSource

abstract class SpiritSpellAction(val onCaster: Boolean) : SpellAction {
    abstract val argCount: Int

    fun pos(i: Int): Int = if(onCaster) i else i+1

    override val argc: Int get() = if(onCaster) argCount else argCount+1

    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        if(onCaster){
            val source=SpiritHelper.spiritSource(ctx.caster)
            return execute(source, args, ctx)
        }
        else{
            val source=args.getSpiritSource(ctx,0,argCount+1)
            return execute(source, args, ctx)
        }
    }

    abstract fun execute(source: SpiritSource, args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>>
}