package jempasam.hexlink.operators.spells

import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import jempasam.hexlink.operators.getSpiritSourceAndPos
import jempasam.hexlink.spirit.inout.SpiritHelper
import jempasam.hexlink.spirit.inout.SpiritSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.Vec3d

abstract class SpiritSpellAction(val onCaster: Boolean) : SpellAction {
    abstract val argCount: Int

    fun pos(i: Int): Int = if(onCaster) i else i+1

    override val argc: Int get() = if(onCaster) argCount else argCount+1

    override fun execute(args: List<Iota>, ctx: CastingEnvironment): SpellAction.Result {
        if(onCaster){
            val caster=ctx.castingEntity
            if(caster !is ServerPlayerEntity) return execute(SpiritSource.NONE, Vec3d.ZERO, args, ctx)
            return execute(SpiritHelper.spiritSource(caster), caster.pos, args, ctx)
        }
        else{
            val source=args.getSpiritSourceAndPos(ctx,0,argCount+1)
            return execute(source.first, source.second, args, ctx)
        }
    }

    abstract fun execute(source: SpiritSource, sourcePos: Vec3d, args: List<Iota>, ctx: CastingEnvironment): SpellAction.Result
}