package jempasam.hexlink.operators.spells

import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getEntity
import at.petrak.hexcasting.api.spell.iota.Iota
import jempasam.hexlink.data.HexlinkConfiguration
import jempasam.hexlink.mishap.MishapNotExtractable
import jempasam.hexlink.operators.getExtractorItemAndPos
import jempasam.hexlink.operators.getSpiritTargetAndPos
import jempasam.hexlink.particle.HexlinkParticles
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.extracter.SpiritExtractor
import jempasam.hexlink.spirit.inout.SpiritTarget
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Vec3d
import kotlin.math.ceil
import kotlin.math.min

class OpExtractSpirit : SpellAction{
    override val argc: Int get() = 3

    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>>{
        val extracted=args.getEntity(0, 3)
        val extractor=args.getExtractorItemAndPos(ctx,1,3)
        val target=args.getSpiritTargetAndPos(ctx,2,3)

        ctx.assertEntityInRange(extracted)
        val extraction=extractor.first.extract(ctx.caster, extracted)
        if(extraction.spirit!=null){
            return Triple(
                    Spell(ctx.world, extractor.first, extraction, target.first, extracted.pos, target.second),
                    HexlinkConfiguration.extractor_settings[extractor.first]?.extraction_media_cost ?: 2,
                    listOf(
                            ParticleSpray.burst(extracted.pos,1.0,2),
                            ParticleSpray.burst(target.second,1.0,2),
                            ParticleSpray.burst(extractor.second,1.0,1)
                    )
            )
        }
        else throw MishapNotExtractable(extracted,extractor.first)
    }

    data class Spell(val world: ServerWorld, val extractor: SpiritExtractor<*>, val extraction: SpiritExtractor.ExtractionResult<*>, val target: SpiritTarget, val from: Vec3d, val to: Vec3d) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            val multiplier=HexlinkConfiguration.extractor_settings[extractor]?.soulCount ?: 1
            val input=target.fill(multiplier*extraction.maxCount, extraction.spirit as Spirit)
            val count= min(input.maxcount, extraction.maxCount*multiplier)
            val consumed= ceil(count.toFloat()/multiplier).toInt()
            extraction.consume(consumed)
            input.fill(count)
            HexlinkParticles.sendLink(world, from, to, extraction.spirit.getColor(), consumed)
        }
    }
}
