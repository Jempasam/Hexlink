package jempasam.hexlink.operators.spells

import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getEntity
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapBadEntity
import jempasam.hexlink.data.HexlinkConfiguration
import jempasam.hexlink.item.functionnality.ExtractorItem
import jempasam.hexlink.mishap.MishapNotExtractable
import jempasam.hexlink.spirit.extracter.ExtractorHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.decoration.ItemFrameEntity
import net.minecraft.item.ItemStack
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

class OpEntityExtractSpirit : SpellAction{
    override val argc: Int get() = 2

    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val extractable_to_entity=args.getEntity(1, 2)
        val target=args.getEntity(0, 2)
        val stack=ExtractorHelper.stack(extractable_to_entity)
        if(stack==null)throw MishapBadEntity(extractable_to_entity,Text.translatable("hexlink.mishap.extractable_to"))
        val item=stack.item
        ctx.assertEntityInRange(target)
        if(item is ExtractorItem){
            if(item.canExtractFrom(stack,target)){
                return Triple(
                        Spell(stack, item, extractable_to_entity, target),
                        HexlinkConfiguration.extractor_settings.get(item.getExtractor(stack))?.extraction_media_cost ?: 500,
                        listOf(
                                ParticleSpray.burst(target.pos,1.0,10),
                                ParticleSpray.burst(extractable_to_entity.pos,1.0,10)
                        )
                )
            }
            else throw MishapNotExtractable(target,stack)
        }
        else throw MishapBadEntity(extractable_to_entity,Text.translatable("hexlink.mishap.extractable_to"))
    }

    data class Spell(val stack: ItemStack, val item: ExtractorItem, val item_entity: Entity, val target: Entity) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            if(item.extractFrom(stack,target)==ExtractorItem.ExtractionResult.SUCCESS){
                if(item_entity is ItemFrameEntity)item_entity.setHeldItemStack(stack,true)
                if(item_entity is ItemEntity)item_entity.stack=stack
            }
            else ctx.caster.sendMessage(Text.translatable("hexlink.unlucky").setStyle(Style.EMPTY.withBold(true).withColor(DyeColor.CYAN.signColor)))

        }
    }
}
