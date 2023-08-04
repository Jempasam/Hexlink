package jempasam.hexlink.operators

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getVec3
import at.petrak.hexcasting.api.spell.iota.BooleanIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import jempasam.hexlink.iota.spiritual.SpiritIota
import net.minecraft.text.Text

class OpSpiritTestPos : ConstMediaAction {
    override val argc: Int
        get() = 2

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val spirit=args.get(0)
        val location=args.getVec3(1)
        if(spirit is SpiritIota){
            return listOf(BooleanIota(spirit.testPos(ctx.world,location)))
        }
        else throw MishapInvalidIota(spirit,1, Text.translatable("hexlink.spirit_iota"))
    }
}