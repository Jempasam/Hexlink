package jempasam.hexlink.operators.spells

import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getIntBetween
import at.petrak.hexcasting.api.spell.iota.EntityIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.Vec3Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import jempasam.hexlink.data.HexlinkConfiguration
import jempasam.hexlink.item.SoulContainerItem
import jempasam.hexlink.mishap.MishapNoEnoughSoul
import jempasam.hexlink.operators.getSpirit
import jempasam.hexlink.spirit.Spirit
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d

class OpManisfestSpirit : SpellAction {

    override val argc: Int get() = 3

    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val spirit=args.getSpirit(0,3)
        val target=args[1]
        val power=args.getIntBetween(2,1,100, 3)
        if(target is Vec3Iota){
            ctx.assertVecInRange(target.vec3)
            val cost=spirit.infuseAtCost(ctx.caster,ctx.world, target.vec3, power)
            if(cost==Spirit.CANNOT_USE)throw MishapInvalidIota(args[0],2, Text.translatable("hexlink.spirit_iota.good"))
            val source=getSource(spirit, ctx.caster)

            return Triple(
                    VecSpell(ctx.world, target.vec3, power, spirit, source),
                    cost*(HexlinkConfiguration.spirit_settings.get(spirit.getType())?.media_cost ?: 5),
                    listOf(ParticleSpray.burst(target.vec3,1.0,5))
            )
        }
        else if(target is EntityIota){
            ctx.assertEntityInRange(target.entity)
            val cost=spirit.infuseInCost(ctx.caster,ctx.world,target.entity,power)
            if(cost==Spirit.CANNOT_USE)throw MishapInvalidIota(args[0],2, Text.translatable("hexlink.spirit_iota.good"))
            val source=getSource(spirit, ctx.caster)

            return Triple(
                    EntitySpell(ctx.world, target.entity, power, spirit, source),
                    cost*(HexlinkConfiguration.spirit_settings.get(spirit.getType())?.media_cost ?: 5),
                    listOf(ParticleSpray.burst(target.entity.pos,1.0,5))
            )

        }
        else throw MishapInvalidIota(target, 1, Text.translatable("hexcasting.iota.hexcasting:entity").append(Text.translatable("hexlink.or")).append(Text.translatable("hexcasting.iota.hexcasting:vec3")))
    }

    fun getSource(spirit: Spirit, caster: PlayerEntity): SoulContainerItem.SoulSource{
        val source=SoulContainerItem.getSpiritConsumable(caster.isCreative, caster.inventory, spirit)
        if(source==null)throw MishapNoEnoughSoul(spirit, 1)
        else return source
    }

    class VecSpell(val world: ServerWorld, val target: Vec3d, val power: Int, val spirit: Spirit, val source: SoulContainerItem.SoulSource) : RenderedSpell{
        override fun cast(ctx: CastingContext) {
            source.consume()
            spirit.infuseAt(ctx.caster,world, target, power)
        }
    }

    class EntitySpell(val world: ServerWorld, val target: Entity, val power: Int, val spirit: Spirit, val source: SoulContainerItem.SoulSource) : RenderedSpell{
        override fun cast(ctx: CastingContext) {
            source.consume()
            spirit.infuseIn(ctx.caster,world, target, power)
        }
    }
}