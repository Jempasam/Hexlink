package jempasam.hexlink.operators.spells

import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getEntity
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.api.spell.mishaps.MishapLocationTooFarAway
import jempasam.hexlink.item.functionnality.ExtractorItem
import jempasam.hexlink.mishap.MishapNotExtractable
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.Hand

class OpExtractSpirit : SpellAction{
    override val argc: Int get() = 1

    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val target=args.getEntity(0, 1)
        val stack=ctx.caster.offHandStack
        val item=stack.item
        if(item is ExtractorItem){
            if(item.canExtractFrom(stack,target)){
                if(target.pos.distanceTo(ctx.position)<30){
                    return Triple(
                            Spell(stack, item, target),
                            500,
                            listOf(
                                    ParticleSpray.burst(target.pos,1.0,10),
                                    ParticleSpray.burst(ctx.position,1.0,10)
                            )
                    )
                }
                else throw MishapLocationTooFarAway(target.pos)
            }
            else throw MishapNotExtractable(target,stack)

        }
        else throw MishapBadOffhandItem(stack, Hand.OFF_HAND, Text.translatable("hexlink.mishap.need_great_focus"))
    }

    private data class Spell(val stack: ItemStack, val item: ExtractorItem, val target: Entity) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            if(item.extractFrom(stack,target)==ExtractorItem.ExtractionResult.SUCCESS){
                target.kill()
            }
            else ctx.caster.sendMessage(Text.translatable("hexlink.unlucky").setStyle(Style.EMPTY.withBold(true).withColor(DyeColor.CYAN.signColor)))

        }
    }
}
