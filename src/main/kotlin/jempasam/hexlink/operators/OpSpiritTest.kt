package jempasam.hexlink.operators

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getEntity
import at.petrak.hexcasting.api.spell.iota.BooleanIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import jempasam.hexlink.iota.spiritual.SpiritIota
import net.minecraft.entity.Entity
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d

class OpSpiritTest : ConstMediaAction {
    override val argc: Int
        get() = 2

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val spirit=args[0]
        val target=args[1]
        if(spirit is SpiritIota){
            if(target is Vec3d){
                return listOf(BooleanIota(spirit.testPos(ctx.world,target)))
            }
            else if(target is Entity){
                return listOf(BooleanIota(spirit.testEntity(ctx.world,target)))
            }
            else throw MishapInvalidIota(target, 1, Text.translatable("hexcasting.iota.hexcasting:entity").append(Text.translatable("hexlink.or")).append(Text.translatable("hexcasting.iota.hexcasting:vec3")));
        }
        else throw MishapInvalidIota(spirit,1, Text.translatable("hexlink.spirit_iota"))
    }
}