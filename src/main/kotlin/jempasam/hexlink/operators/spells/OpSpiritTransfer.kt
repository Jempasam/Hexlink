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
import jempasam.hexlink.particle.HexlinkParticles
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.inout.SpiritSource
import jempasam.hexlink.spirit.inout.SpiritTarget
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Vec3d

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
                        Spell(ctx.world, sourceFlux, targetFlux, targetFlux.maxcount, spirit, source.second, target.second),
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
    }

    class Spell(val world: ServerWorld, val output: SpiritSource.SpiritOutputFlux, val input: SpiritTarget.SpiritInputFlux, val count: Int, val spirit: Spirit, val from: Vec3d, val to: Vec3d): RenderedSpell{
        override fun cast(ctx: CastingContext) {
            output.consume(count)
            input.fill(count)
            HexlinkParticles.sendLink(world, from, to, spirit.getColor(), count)
        }
    }
}