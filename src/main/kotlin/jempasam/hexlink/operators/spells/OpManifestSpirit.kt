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
import jempasam.hexlink.mishap.MishapNotManifestable
import jempasam.hexlink.operators.getSpirit
import jempasam.hexlink.particle.HexlinkParticles
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.inout.SpiritSource
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d

class OpManisfestSpirit(oncaster: Boolean) : SpiritSpellAction(oncaster) {

    override val argCount: Int get() = 3

    override fun execute(source: SpiritSource, sourcePos: Vec3d, args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
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
            else -> throw MishapInvalidIota(target, 0, Text.translatable("hexcasting.iota.hexcasting:entity").append(Text.translatable("hexlink.or")).append(Text.translatable("hexcasting.iota.hexcasting:vec3")))
        }

        if(manifestation.minimumCount>input.maxcount)throw MishapNoEnoughSoul(spirit,manifestation.minimumCount)
        if(manifestation.spiritCount==0)throw MishapNotManifestable(spirit, target)
        val manisfestedAmount=Math.min(manifestation.spiritCount,input.maxcount)

        return Triple(
            ManifestSpell(ctx.world, manifestation, input, sourcePos, targetPos, spirit, manisfestedAmount),
            manifestation.maxMediaCost*(HexlinkConfiguration.spirit_settings[spirit.getType()]?.media_cost ?: 5),
            listOf(ParticleSpray.burst(targetPos,1.0,1))
        )
    }

    class ManifestSpell(val world: ServerWorld, val manifestation: Spirit.Manifestation, val source: SpiritSource.SpiritOutputFlux, val from: Vec3d, val to: Vec3d, val spirit: Spirit, val count: Int) : RenderedSpell{
        override fun cast(ctx: CastingContext) {
            source.consume(count)
            manifestation.execute(count)
            HexlinkParticles.sendLink(world, from, to, spirit.getColor(), count)
        }
    }
}