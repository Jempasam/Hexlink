package jempasam.hexlink.operators

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.BooleanIota
import at.petrak.hexcasting.api.spell.iota.EntityIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.Vec3Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import jempasam.hexlink.iota.SpiritIota
import net.minecraft.text.Text

class OpSpiritTest : ConstMediaAction {
    override val argc: Int
        get() = 2

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val spirit=args[0]
        val target=args[1]
        return if(spirit is SpiritIota){
            when (target) {
                is Vec3Iota ->
                    listOf(BooleanIota(spirit.getSpirit().lookAt(ctx.caster,ctx.world,target.vec3)))
                is EntityIota ->
                    listOf(BooleanIota(spirit.getSpirit().lookIn(ctx.caster,ctx.world,target.entity)))
                else ->
                    throw MishapInvalidIota(target, 1, Text.translatable("hexcasting.iota.hexcasting:entity").append(Text.translatable("hexlink.or")).append(Text.translatable("hexcasting.iota.hexcasting:vec3")))
            }
        }
        else throw MishapInvalidIota(spirit,1, Text.translatable("hexlink.spirit_iota"))
    }
}