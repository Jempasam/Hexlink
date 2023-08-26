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
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.extracter.SpiritExtractor
import jempasam.hexlink.spirit.inout.SpiritTarget
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
                    Spell(extractor.first, extraction, target.first),
                    HexlinkConfiguration.extractor_settings[extractor.first]?.extraction_media_cost ?: 2,
                    listOf(
                            ParticleSpray.burst(extracted.pos,1.0,5),
                            ParticleSpray.burst(target.second,1.0,5),
                            ParticleSpray.burst(extractor.second,1.0,5)
                    )
            )
        }
        else throw MishapNotExtractable(extracted,extractor.first)
    }

    data class Spell(val extractor: SpiritExtractor<*>, val extraction: SpiritExtractor.ExtractionResult<*>, val target: SpiritTarget) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            val multiplier=HexlinkConfiguration.extractor_settings[extractor]?.soul_count ?: 1
            val input=target.fill(multiplier*extraction.max_count, extraction.spirit as Spirit)
            val count= min(input.maxcount, extraction.max_count*multiplier)
            val consumed= ceil(count.toFloat()/multiplier).toInt()
            extraction.consume(consumed)
            input.fill(count)
        }
    }
}
