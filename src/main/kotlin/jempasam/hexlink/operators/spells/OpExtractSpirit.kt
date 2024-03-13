package jempasam.hexlink.operators.spells

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import jempasam.hexlink.mishap.MishapNotExtractable
import jempasam.hexlink.operators.getExtractorItemAndPos
import jempasam.hexlink.operators.getSpiritTargetAndPos
import jempasam.hexlink.particle.HexlinkParticles
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.extractor.SpiritExtractor
import jempasam.hexlink.spirit.inout.SpiritTarget
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Vec3d
import kotlin.math.min

class OpExtractSpirit : SpellAction{
    override val argc: Int get() = 3

    override fun execute(args: List<Iota>, ctx: CastingEnvironment): SpellAction.Result{
        val extracted=args.getEntity(0, 3)
        val extractor=args.getExtractorItemAndPos(ctx,1,3)
        val target=args.getSpiritTargetAndPos(ctx,2,3)

        ctx.assertEntityInRange(extracted)
        val extraction=extractor.first.extract(ctx.caster, extracted)
        if(extraction.spirit!=null){
            return SpellAction.Result(
                    Spell(ctx.world, extractor.first, extraction, target.first, extracted.pos, target.second),
                    extractor.first.getCost().toLong(),
                    listOf(
                            ParticleSpray.burst(extracted.pos,1.0,1),
                            ParticleSpray.burst(target.second,1.0,1),
                            ParticleSpray.burst(extractor.second,1.0,1)
                    )
            )
        }
        else throw MishapNotExtractable(extracted,extractor.first)
    }

    data class Spell(val world: ServerWorld, val extractor: SpiritExtractor<*>, val extraction: SpiritExtractor.ExtractionResult<*>, val target: SpiritTarget, val from: Vec3d, val to: Vec3d) : RenderedSpell {
        override fun cast(ctx: CastingEnvironment) {
            val input=target.fill(extraction.maxCount, extraction.spirit as Spirit)
            val count= min(input.maxcount, extraction.maxCount)
            val consumed=count
            extraction.consume(consumed)
            input.fill(count)
            HexlinkParticles.sendLink(world, from, to, extraction.spirit.getColor(), consumed)
        }
    }
}
