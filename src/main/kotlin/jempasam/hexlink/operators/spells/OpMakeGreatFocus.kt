package jempasam.hexlink.operators.spells;

import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.api.spell.mishaps.MishapLocationTooFarAway
import jempasam.hexlink.item.GreatFocusItem
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Hand

class OpMakeGreatFocus : SpellAction{
    override val argc: Int get() = 1

    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>>? {
        val target=args.getEntity(0, 1)
        val stack=ctx.caster.offHandStack
        val item=stack.item
        if(item is GreatFocusItem<*> && item.canWriteEntity(stack,target)){
            if(target.pos.distanceTo(ctx.position)<30){
                return Triple(
                        Spell(stack, item, target),
                        500,
                        listOf(
                                ParticleSpray.burst(target.pos,1.0,1),
                                ParticleSpray.burst(ctx.position,1.0,1)
                        )
                )
            }
            else throw MishapLocationTooFarAway(target.pos)
        }
        else throw MishapBadOffhandItem(stack, Hand.OFF_HAND, Text.translatable("hexlink.mishap.need_great_focus"))
    }

    private data class Spell<T: Iota>(val stack: ItemStack, val item: GreatFocusItem<T>, val target: Entity) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            item.writeEntity(stack,target)
        }
    }
}
