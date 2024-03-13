package jempasam.hexlink.operators.spiritinfo

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.spell.iota.NullIota
import jempasam.hexlink.iota.SpiritIota
import jempasam.hexlink.spirit.inout.SpiritSource

class OpGetSpiritIota(onCaster: Boolean) : SpiritConstMediaAction(onCaster) {

    override val argCount: Int get() = 0

    override fun execute(source: SpiritSource?, args: List<Iota>, ctx: CastingEnvironment): List<Iota> {
        return listOf(source?.last()?.let { SpiritIota(it) } ?: NullIota())
    }
}