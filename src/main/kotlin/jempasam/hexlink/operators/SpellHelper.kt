package jempasam.hexlink.operators

import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.EntityIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.Vec3Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import jempasam.hexlink.iota.SpiritIota
import jempasam.hexlink.mishap.InvalidSpiritSource
import jempasam.hexlink.mishap.InvalidSpiritTarget
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.inout.SpiritHelper
import jempasam.hexlink.spirit.inout.SpiritSource
import jempasam.hexlink.spirit.inout.SpiritTarget
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d

fun List<Iota>.getSpirit(index: Int, max: Int): Spirit{
    val ret=this.get(index)
    if(ret is SpiritIota)return ret.getSpirit()
    else throw MishapInvalidIota(ret, max-index-1, Text.translatable("hexlink.spirit_iota"))
}

fun List<Iota>.getSpiritSource(ctx: CastingContext, index: Int, max: Int): SpiritSource{
    val source_iota=get(index)
    val source=when(source_iota){
        is EntityIota ->{
            ctx.assertEntityInRange(source_iota.entity)
            SpiritHelper.spiritSource(ctx.caster,source_iota.entity)
        }
        is Vec3Iota ->{
            ctx.assertVecInRange(source_iota.vec3)
            SpiritHelper.spiritSource(ctx.caster, ctx.world, source_iota.vec3)
        }
        else -> throw MishapInvalidIota(get(index), max-index+1, Text.translatable("hexcasting.iota.hexcasting:entity").append(Text.translatable("hexlink.or")).append(Text.translatable("hexcasting.iota.hexcasting:vec3")))
    }
    return source ?: throw InvalidSpiritSource(get(index))
}

fun List<Iota>.getSpiritSourceAndPos(ctx: CastingContext, index: Int, max: Int): Pair<SpiritSource,Vec3d>{
    val source=getSpiritSource(ctx,index,max)
    val pos=when(val iota=get(index)){
        is EntityIota -> iota.entity.pos
        is Vec3Iota -> iota.vec3
        else -> Vec3d.ZERO
    }
    return source to pos
}

fun List<Iota>.getSpiritTarget(ctx: CastingContext, index: Int, max: Int): SpiritTarget{
    val target_iota=get(index)
    val target=when(target_iota){
        is EntityIota ->{
            ctx.assertEntityInRange(target_iota.entity)
            SpiritHelper.spiritTarget(ctx.caster,target_iota.entity)
        }
        is Vec3Iota ->{
            ctx.assertVecInRange(target_iota.vec3)
            SpiritHelper.spiritTarget(ctx.caster, ctx.world, target_iota.vec3)
        }
        else -> throw MishapInvalidIota(get(index), max-index+1, Text.translatable("hexcasting.iota.hexcasting:entity").append(Text.translatable("hexlink.or")).append(Text.translatable("hexcasting.iota.hexcasting:vec3")))
    }
    return target ?: throw InvalidSpiritTarget(get(index))
}

fun List<Iota>.getSpiritTargetAndPos(ctx: CastingContext, index: Int, max: Int): Pair<SpiritTarget,Vec3d>{
    val target=getSpiritTarget(ctx,index,max)
    val pos=when(val iota=get(index)){
        is EntityIota -> iota.entity.pos
        is Vec3Iota -> iota.vec3
        else -> Vec3d.ZERO
    }
    return target to pos
}