package jempasam.hexlink.operators.spells

import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.spell.mishaps.MishapOthersName
import jempasam.hexlink.item.functionnality.SpellHolderItem
import jempasam.hexlink.spirit.StackHelper
import net.minecraft.text.Text

class OpFillSpell(val cost: Int) : SpellAction {
    override val argc: Int get() = 2

    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val entity=args.getEntity(0, argc)
        val spell=args.getList(1,argc).toList()

        val world_stack=StackHelper.stack(ctx.caster,entity)

        if(world_stack==null || world_stack.stack.item !is SpellHolderItem){
            throw MishapBadEntity(entity, Text.translatable("hexlink.mishap.spell_holder"))
        }

        MishapOthersName.getTrueNameFromArgs(spell,ctx.caster)?.let { throw MishapOthersName(it) }

        return Triple(
                Spell(world_stack,spell),
                cost,
                listOf(ParticleSpray.burst(entity.pos, 0.5))
        )
    }

    class Spell(val worldStack: StackHelper.WorldStack, val spell: List<Iota>): RenderedSpell{
        override fun cast(ctx: CastingContext) {
            val item=worldStack.stack.item as SpellHolderItem
            item.setSpell(worldStack.stack,spell)
            worldStack.update()
        }
    }
}