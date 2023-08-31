package jempasam.hexlink.operators.spells

import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import jempasam.hexlink.operators.getSpiritSourceAndPos
import jempasam.hexlink.spirit.inout.SpiritHelper
import jempasam.hexlink.spirit.inout.SpiritSource
import net.minecraft.util.math.Vec3d

abstract class SpiritSpellAction(val onCaster: Boolean) : SpellAction {
    abstract val argCount: Int

    fun pos(i: Int): Int = if(onCaster) i else i+1

    override val argc: Int get() = if(onCaster) argCount else argCount+1

    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        if(onCaster){
            val source=SpiritHelper.spiritSource(ctx.caster)
            return execute(source, ctx.caster.pos, args, ctx)
        }
        else{
            val source=args.getSpiritSourceAndPos(ctx,0,argCount+1)
            return execute(source.first, source.second, args, ctx)
        }
    }

    abstract fun execute(source: SpiritSource, sourcePos: Vec3d, args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>>
}