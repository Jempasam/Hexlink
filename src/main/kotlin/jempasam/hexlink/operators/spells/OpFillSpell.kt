package jempasam.hexlink.operators.spells

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.getList
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.casting.mishaps.MishapOthersName
import jempasam.hexlink.item.functionnality.SpellHolderItem
import jempasam.hexlink.spirit.StackHelper
import net.minecraft.text.Text

class OpFillSpell(val cost: Int) : SpellAction {
    override val argc: Int get() = 2

    override fun execute(args: List<Iota>, ctx: CastingEnvironment): SpellAction.Result {
        val entity=args.getEntity(0, argc)
        val spell=args.getList(1,argc).toList()

        val world_stack=StackHelper.stack(ctx.caster,entity)

        if(world_stack==null || world_stack.stack.item !is SpellHolderItem){
            throw MishapBadEntity(entity, Text.translatable("hexlink.mishap.spell_holder"))
        }

        MishapOthersName.getTrueNameFromArgs(spell,ctx.caster)?.let { throw MishapOthersName(it) }

        return SpellAction.Result(
                Spell(world_stack,spell),
                cost.toLong(),
                listOf(ParticleSpray.burst(entity.pos, 0.5))
        )
    }

    class Spell(val worldStack: StackHelper.WorldStack, val spell: List<Iota>): RenderedSpell{
        override fun cast(ctx: CastingEnvironment) {
            val item=worldStack.stack.item as SpellHolderItem
            item.setSpell(worldStack.stack,spell)
            worldStack.update()
        }
    }
}