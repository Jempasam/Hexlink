package jempasam.hexlink.operators.spiritinfo

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.spell.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import jempasam.hexlink.operators.getSpirit
import jempasam.hexlink.spirit.inout.SpiritSource

class OpSpiritCountIota(onCaster: Boolean) : SpiritConstMediaAction(onCaster) {

    override val argCount: Int get() = 1
    override fun execute(source: SpiritSource?, args: List<Iota>, ctx: CastingEnvironment): List<Iota> {
        val type=args.getSpirit(pos(0),argc)
        return listOf(source?.extract(Int.MAX_VALUE,type)?.maxcount?.let { DoubleIota(it.toDouble()) } ?: DoubleIota(0.0))
    }
}