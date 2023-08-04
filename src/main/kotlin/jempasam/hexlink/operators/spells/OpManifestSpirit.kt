package jempasam.hexlink.operators.spells

import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.EntityIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.Vec3Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapEntityTooFarAway
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.spell.mishaps.MishapLocationTooFarAway
import jempasam.hexlink.iota.spiritual.SpiritIota
import net.minecraft.entity.Entity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d

class OpManisfestSpirit : SpellAction {

    override val argc: Int get() = 3

    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>>? {
        val spirit=args[0]
        val target=args[1]
        val power=args.getIntBetween(2,1,100)
        if(spirit is SpiritIota){
            if(target is Vec3Iota){
                if(ctx.position.distanceTo(target.vec3)>30)throw MishapLocationTooFarAway(target.vec3)
                val cost=spirit.canDrop(ctx.world, target.vec3, power)
                if(cost==SpiritIota.CANNOT_DO)throw MishapInvalidIota(spirit,2, Text.translatable("hexlink.spirit_iota.good"))
                return Triple(
                        VecSpell(ctx.world, target.vec3, power, spirit),
                        cost,
                        listOf(ParticleSpray.burst(target.vec3,1.0,5))
                )
            }
            else if(target is EntityIota){
                if(ctx.position.distanceTo(target.entity.pos)>30)throw MishapEntityTooFarAway(target.entity)
                val cost=spirit.canInfuse(ctx.world,target.entity,power)
                if(cost==SpiritIota.CANNOT_DO)throw MishapInvalidIota(spirit,2, Text.translatable("hexlink.spirit_iota.good"))
                return Triple(
                        EntitySpell(ctx.world, target.entity, power, spirit),
                        cost,
                        listOf(ParticleSpray.burst(target.entity.pos,1.0,5))
                )

            }
            else throw MishapInvalidIota(target, 1, Text.translatable("hexcasting.iota.hexcasting:entity").append(Text.translatable("hexlink.or")).append(Text.translatable("hexcasting.iota.hexcasting:vec3")));
        }
        else throw MishapInvalidIota(spirit, 2, Text.translatable("hexlink.spirit_iota"))
    }

    class VecSpell(val world: ServerWorld, val target: Vec3d, val power: Int, val spirit: SpiritIota) : RenderedSpell{
        override fun cast(ctx: CastingContext) {
            spirit.drop(world, target, power)
        }
    }

    class EntitySpell(val world: ServerWorld, val target: Entity, val power: Int, val spirit: SpiritIota) : RenderedSpell{
        override fun cast(ctx: CastingContext) {
            spirit.infuse(world, target, power)
        }
    }
}