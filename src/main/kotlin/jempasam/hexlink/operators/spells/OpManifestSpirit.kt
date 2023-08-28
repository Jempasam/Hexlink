package jempasam.hexlink.operators.spells

import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getIntBetween
import at.petrak.hexcasting.api.spell.iota.EntityIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.Vec3Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import jempasam.hexlink.data.HexlinkConfiguration
import jempasam.hexlink.mishap.MishapNoEnoughSoul
import jempasam.hexlink.operators.getSpirit
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.inout.SpiritSource
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text

class OpManisfestSpirit(oncaster: Boolean) : SpiritSpellAction(oncaster) {

    override val argCount: Int get() = 3

    override fun execute(source: SpiritSource, args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val spirit=args.getSpirit(pos(0),argc)
        val power=args.getIntBetween(pos(1), 1, 100, argc)
        val target=args[pos(2)]

        val input=source.extract(power,spirit)
        if(input.maxcount==0)throw MishapNoEnoughSoul(spirit,1)

        val (manifestation,targetPos)=when(target){
            is Vec3Iota ->{
                ctx.assertVecInRange(target.vec3)
                spirit.manifestAt(ctx.caster,ctx.world, target.vec3, input.maxcount) to target.vec3
            }
            is EntityIota ->{
                ctx.assertEntityInRange(target.entity)
                spirit.manifestIn(ctx.caster,ctx.world, target.entity, input.maxcount) to target.entity.pos
            }
            else -> throw MishapInvalidIota(target, 1, Text.translatable("hexcasting.iota.hexcasting:entity").append(Text.translatable("hexlink.or")).append(Text.translatable("hexcasting.iota.hexcasting:vec3")))
        }

        return Triple(
            ManifestSpell(ctx.world, manifestation, input),
            manifestation.maxMediaCost*(HexlinkConfiguration.spirit_settings[spirit.getType()]?.media_cost ?: 5),
            listOf(ParticleSpray.burst(targetPos,1.0,input.maxcount))
        )
    }

    class ManifestSpell(val world: ServerWorld, val manifestation: Spirit.Manifestation, val source: SpiritSource.SpiritOutputFlux) : RenderedSpell{
        override fun cast(ctx: CastingContext) {
            source.consume(1)
            manifestation.execute(manifestation.spiritCount)
        }
    }
}