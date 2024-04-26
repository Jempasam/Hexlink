package jempasam.hexlink.operators.spiritinfo

import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.ListIota
import jempasam.hexlink.iota.SpiritIota
import jempasam.hexlink.spirit.inout.SpiritSource

class OpGetSpiritListIota(onCaster: Boolean) : SpiritConstMediaAction(onCaster) {

    override val argCount: Int get() = 0

    override fun execute(source: SpiritSource?, args: List<Iota>, ctx: CastingContext): List<Iota> {
        return listOf(source ?.all() ?.map{SpiritIota(it)} ?.let{ListIota(it)} ?: ListIota(mutableListOf()))
    }
}