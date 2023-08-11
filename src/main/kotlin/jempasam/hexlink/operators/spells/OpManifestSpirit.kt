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
import jempasam.hexlink.iota.SpiritIota
import jempasam.hexlink.item.SoulContainerItem
import jempasam.hexlink.mishap.MishapNoEnoughSoul
import jempasam.hexlink.spirit.Spirit
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d

class OpManisfestSpirit : SpellAction {

    override val argc: Int get() = 3

    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val spirit=args[0]
        val target=args[1]
        val power=args.getIntBetween(2,1,100, 3)
        if(spirit is SpiritIota){
            if(target is Vec3Iota){
                ctx.assertVecInRange(target.vec3)
                val cost=spirit.getSpirit().infuseAtCost(ctx.caster,ctx.world, target.vec3, power)
                if(cost==Spirit.CANNOT_USE)throw MishapInvalidIota(spirit,2, Text.translatable("hexlink.spirit_iota.good"))
                val bag=getBagIfNeeded(spirit.getSpirit(), ctx.caster)

                return Triple(
                        VecSpell(ctx.world, target.vec3, power, spirit.getSpirit(), bag),
                        cost*(HexlinkConfiguration.spirit_settings.get(spirit.getSpirit().getType())?.media_cost ?: 5),
                        listOf(ParticleSpray.burst(target.vec3,1.0,5))
                )
            }
            else if(target is EntityIota){
                ctx.assertEntityInRange(target.entity)
                val cost=spirit.getSpirit().infuseInCost(ctx.caster,ctx.world,target.entity,power)
                if(cost==Spirit.CANNOT_USE)throw MishapInvalidIota(spirit,2, Text.translatable("hexlink.spirit_iota.good"))
                val bag=getBagIfNeeded(spirit.getSpirit(), ctx.caster)

                return Triple(
                        EntitySpell(ctx.world, target.entity, power, spirit.getSpirit(), bag),
                        cost*(HexlinkConfiguration.spirit_settings.get(spirit.getSpirit().getType())?.media_cost ?: 5),
                        listOf(ParticleSpray.burst(target.entity.pos,1.0,5))
                )

            }
            else throw MishapInvalidIota(target, 1, Text.translatable("hexcasting.iota.hexcasting:entity").append(Text.translatable("hexlink.or")).append(Text.translatable("hexcasting.iota.hexcasting:vec3")))
        }
        else throw MishapInvalidIota(spirit, 2, Text.translatable("hexlink.spirit_iota"))
    }

    fun getBagIfNeeded(spirit: Spirit, caster: PlayerEntity): ItemStack?{
        if(caster.isCreative)return null
        val need_soul= HexlinkConfiguration.spirit_settings.get(spirit.getType())?.use_soul ?: true
        if(need_soul){
            val bag=SoulContainerItem.getSpiritConsumable(caster.inventory,spirit)
            if(bag==null || !(bag.item as SoulContainerItem).consumeSpirit(bag,spirit))
                throw MishapNoEnoughSoul(spirit, 1)
            return bag
        }
        return null
    }

    class VecSpell(val world: ServerWorld, val target: Vec3d, val power: Int, val spirit: Spirit, val stack: ItemStack?) : RenderedSpell{
        override fun cast(ctx: CastingContext) {
            if(stack!=null){
                val item=stack.item
                if(item is SoulContainerItem)item.consumeSpirit(stack,spirit)
            }
            spirit.infuseAt(ctx.caster,world, target, power)
        }
    }

    class EntitySpell(val world: ServerWorld, val target: Entity, val power: Int, val spirit: Spirit, val stack: ItemStack?) : RenderedSpell{
        override fun cast(ctx: CastingContext) {
            if(stack!=null){
                val item=stack.item
                if(item is SoulContainerItem)item.consumeSpirit(stack,spirit)
            }
            spirit.infuseIn(ctx.caster,world, target, power)
        }
    }
}