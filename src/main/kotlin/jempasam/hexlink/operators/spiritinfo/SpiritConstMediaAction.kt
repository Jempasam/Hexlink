package jempasam.hexlink.operators.spiritinfo

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import jempasam.hexlink.operators.getSpiritSourceOpt
import jempasam.hexlink.spirit.inout.SpiritHelper
import jempasam.hexlink.spirit.inout.SpiritSource

abstract class SpiritConstMediaAction(val onCaster: Boolean) : ConstMediaAction {
    abstract val argCount: Int

    fun pos(i: Int): Int = if(onCaster) i else i+1

    override val argc: Int get() = if(onCaster) argCount else argCount+1

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        if(onCaster){
            val source=SpiritHelper.spiritSource(ctx.caster)
            return execute(source, args, ctx)
        }
        else{
            val source=args.getSpiritSourceOpt(ctx,0,argCount+1)
            return execute(source, args, ctx)
        }
    }

    abstract fun execute(source: SpiritSource?, args: List<Iota>, ctx: CastingContext): List<Iota>
}