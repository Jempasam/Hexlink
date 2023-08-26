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
import net.minecraft.entity.Entity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d

class OpManisfestSpirit(oncaster: Boolean) : SpiritSpellAction(oncaster) {

    override val argCount: Int get() = 3

    override fun execute(source: SpiritSource, args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val spirit=args.getSpirit(pos(0),argc)
        val target=args[pos(1)]
        val power=args.getIntBetween(pos(2),1,100, argc)

        val input=source.extract(1,spirit)
        if(input.maxcount==0)throw MishapNoEnoughSoul(spirit,1)

        if(target is Vec3Iota){
            ctx.assertVecInRange(target.vec3)
            val cost=spirit.infuseAtCost(ctx.caster,ctx.world, target.vec3, power)
            if(cost==Spirit.CANNOT_USE)throw MishapInvalidIota(args[0],2, Text.translatable("hexlink.spirit_iota.good"))

            return Triple(
                    VecSpell(ctx.world, target.vec3, power, spirit, input),
                    cost*(HexlinkConfiguration.spirit_settings[spirit.getType()]?.media_cost ?: 5),
                    listOf(ParticleSpray.burst(target.vec3,1.0,5))
            )
        }
        else if(target is EntityIota){
            ctx.assertEntityInRange(target.entity)
            val cost=spirit.infuseInCost(ctx.caster,ctx.world,target.entity,power)
            if(cost==Spirit.CANNOT_USE)throw MishapInvalidIota(args[0],2, Text.translatable("hexlink.spirit_iota.good"))

            return Triple(
                    EntitySpell(ctx.world, target.entity, power, spirit, input),
                    cost*(HexlinkConfiguration.spirit_settings[spirit.getType()]?.media_cost ?: 5),
                    listOf(ParticleSpray.burst(target.entity.pos,1.0,5))
            )

        }
        else throw MishapInvalidIota(target, 1, Text.translatable("hexcasting.iota.hexcasting:entity").append(Text.translatable("hexlink.or")).append(Text.translatable("hexcasting.iota.hexcasting:vec3")))
    }

    class VecSpell(val world: ServerWorld, val target: Vec3d, val power: Int, val spirit: Spirit, val source: SpiritSource.SpiritOutputFlux) : RenderedSpell{
        override fun cast(ctx: CastingContext) {
            source.consume(1)
            spirit.infuseAt(ctx.caster,world, target, power)
        }
    }

    class EntitySpell(val world: ServerWorld, val target: Entity, val power: Int, val spirit: Spirit, val source: SpiritSource.SpiritOutputFlux) : RenderedSpell{
        override fun cast(ctx: CastingContext) {
            source.consume(1)
            spirit.infuseIn(ctx.caster,world, target, power)
        }
    }
}