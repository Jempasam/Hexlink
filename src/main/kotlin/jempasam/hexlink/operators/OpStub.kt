package jempasam.hexlink.operators

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import net.minecraft.text.Text

class OpStub(val modid: String) : ConstMediaAction {
    override val argc: Int = 0

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        ctx.caster.sendMessage(Text.of("You hear the breath of ancient dark forces whispering disturbing words to you in a forgotten language: \"For this pattern, you must download $modid\""))
        return listOf()
    }
}