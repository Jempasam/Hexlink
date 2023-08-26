package jempasam.hexlink.operators.spiritinfo

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.NullIota
import jempasam.hexlink.iota.SpiritIota
import jempasam.hexlink.operators.getSpiritSourceOpt

class OpGetSpiritIota : ConstMediaAction {

    override val argc: Int get() = 1
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val source=args.getSpiritSourceOpt(ctx,0,1)
        return listOf(source?.last()?.let { SpiritIota(it) } ?: NullIota())
    }
}