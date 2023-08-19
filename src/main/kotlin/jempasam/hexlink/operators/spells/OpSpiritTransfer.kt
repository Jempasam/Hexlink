package jempasam.hexlink.operators.spells

import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getIntBetween
import at.petrak.hexcasting.api.spell.iota.Iota
import jempasam.hexlink.mishap.MishapNoEnoughSoul
import jempasam.hexlink.operators.getSpirit
import jempasam.hexlink.operators.getSpiritSource
import jempasam.hexlink.operators.getSpiritTarget

class OpSpiritTransfer: SpellAction {
    override val argc: Int
        get() = 4

    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>>? {
        val spirit=args.getSpirit(0,4)
        val count=args.getIntBetween(1, 1, Int.MAX_VALUE, 4)
        val source=args.getSpiritSource(ctx,2,4)
        val target=args.getSpiritTarget(ctx,3,4)

        val test_output_flux=source.extract(count,spirit)
        if(test_output_flux!=null){
            val input_flux=target.fill(test_output_flux.count,spirit)
            if(input_flux!=null){
                val output_flux=source.extract(input_flux.count,spirit)
                if(output_flux!=null){
                    input_flux.fill()
                    output_flux.consume()
                }
            }
            else throw MishapNoEnoughSoul(spirit,1)
        }
        throw MishapNoEnoughSoul(spirit,-1)
    }
}