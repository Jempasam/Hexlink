package jempasam.hexlink.operators

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import net.minecraft.text.Text

class OpStub(val modid: String) : ConstMediaAction {
    override val argc: Int = 0

    override fun execute(args: List<Iota>, ctx: CastingEnvironment): List<Iota> {
        ctx.castingEntity?.sendMessage(Text.of("You hear the breath of ancient dark forces whispering disturbing words to you in a forgotten language: \"For this pattern, you must download $modid\""))
        return listOf()
    }
}