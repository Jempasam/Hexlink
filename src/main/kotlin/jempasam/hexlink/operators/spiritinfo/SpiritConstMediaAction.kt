package jempasam.hexlink.operators.spiritinfo

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import jempasam.hexlink.operators.getSpiritSourceOpt
import jempasam.hexlink.spirit.inout.SpiritHelper
import jempasam.hexlink.spirit.inout.SpiritSource
import net.minecraft.server.network.ServerPlayerEntity

abstract class SpiritConstMediaAction(val onCaster: Boolean) : ConstMediaAction {
    abstract val argCount: Int

    fun pos(i: Int): Int = if(onCaster) i else i+1

    override val argc: Int get() = if(onCaster) argCount else argCount+1

    override fun execute(args: List<Iota>, ctx: CastingEnvironment): List<Iota> {
        if(onCaster){
            val source=(ctx.castingEntity as? ServerPlayerEntity)
                ?.let { SpiritHelper.spiritSource(it)}
                ?: SpiritSource.NONE
            return execute(source, args, ctx)
        }
        else{
            val source=args.getSpiritSourceOpt(ctx,0,argCount+1)
            return execute(source, args, ctx)
        }
    }

    abstract fun execute(source: SpiritSource?, args: List<Iota>, ctx: CastingEnvironment): List<Iota>
}