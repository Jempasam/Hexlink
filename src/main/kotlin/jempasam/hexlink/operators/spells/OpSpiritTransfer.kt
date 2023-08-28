package jempasam.hexlink.operators.spells

import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getIntBetween
import at.petrak.hexcasting.api.spell.iota.Iota
import jempasam.hexlink.mishap.MishapNoEnoughSoul
import jempasam.hexlink.operators.getSpirit
import jempasam.hexlink.operators.getSpiritSourceAndPos
import jempasam.hexlink.operators.getSpiritTargetAndPos
import jempasam.hexlink.spirit.inout.SpiritSource
import jempasam.hexlink.spirit.inout.SpiritTarget

class OpSpiritTransfer: SpellAction {
    override val argc: Int
        get() = 4

    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val spirit=args.getSpirit(0,4)
        val count=args.getIntBetween(1, 1, Int.MAX_VALUE, 4)
        val source=args.getSpiritSourceAndPos(ctx,2,4)
        val target=args.getSpiritTargetAndPos(ctx,3,4)

        val sourceFlux=source.first.extract(count, spirit)
        if(sourceFlux.maxcount>0){
            val targetFlux=target.first.fill(sourceFlux.maxcount, spirit)
            if(targetFlux.maxcount>0){
                return Triple(
                        Spell(sourceFlux, targetFlux, targetFlux.maxcount),
                        1,
                        listOf(
                                ParticleSpray.burst(source.second,0.5, 5),
                                ParticleSpray.burst(target.second,0.5, 5)
                        )
                )
            }
            else throw MishapNoEnoughSoul(spirit, -1)
        }
        else throw MishapNoEnoughSoul(spirit, 1)

        /*val test_output_flux=source.first.extract(count,spirit)
        if(test_output_flux.count>0){
            println("test_output_flux")
            val input_flux=target.first.fill(test_output_flux.count,spirit)
            if(input_flux.count>0){
                println("input_flux")
                val output_flux=source.first.extract(input_flux.count,spirit)
                if(output_flux.count>0){
                    println("output_flux")
                    return Triple(
                            Spell(output_flux,input_flux),
                            1,
                            listOf(
                                    ParticleSpray.burst(source.second,0.5, 5),
                                    ParticleSpray.burst(target.second,0.5, 5)
                            )
                    )
                }
            }
            else throw MishapNoEnoughSoul(spirit,1)
        }
        throw MishapNoEnoughSoul(spirit,-1)*/
    }

    class Spell(val output: SpiritSource.SpiritOutputFlux, val input: SpiritTarget.SpiritInputFlux, val count: Int): RenderedSpell{
        override fun cast(ctx: CastingContext) {
            output.consume(count)
            input.fill(count)
        }
    }
}