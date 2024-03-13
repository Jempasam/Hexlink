package jempasam.hexlink.operators

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import jempasam.hexlink.iota.SpiritIota
import jempasam.hexlink.item.functionnality.ExtractorItem
import jempasam.hexlink.mishap.InvalidSpiritSource
import jempasam.hexlink.mishap.InvalidSpiritTarget
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.StackHelper
import jempasam.hexlink.spirit.extractor.SpiritExtractor
import jempasam.hexlink.spirit.inout.SpiritHelper
import jempasam.hexlink.spirit.inout.SpiritSource
import jempasam.hexlink.spirit.inout.SpiritTarget
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

fun <T>List<Iota>.makePosPair(first: T, index: Int): Pair<T,Vec3d>{
    val pos=when(val iota=get(index)){
        is EntityIota -> iota.entity.pos
        is Vec3Iota -> iota.vec3
        else -> Vec3d.ZERO
    }
    return first to pos
}

fun List<Iota>.getSpirit(index: Int, max: Int): Spirit{
    val ret= this[index]
    if(ret is SpiritIota)return ret.getSpirit()
    else throw MishapInvalidIota(ret, max-index-1, Text.translatable("hexlink.spirit_iota"))
}

fun List<Iota>.getSpiritSourceOpt(ctx: CastingEnvironment, index: Int, max: Int): SpiritSource?{
    val source=when(val sourceIota=get(index)){
        is EntityIota ->{
            ctx.assertEntityInRange(sourceIota.entity)
            SpiritHelper.spiritSource(ctx.caster,sourceIota.entity)
        }
        is Vec3Iota ->{
            ctx.assertVecInRange(sourceIota.vec3)
            SpiritHelper.spiritSource(ctx.caster, ctx.world, sourceIota.vec3)
        }
        else -> throw MishapInvalidIota(get(index), max-index+1, Text.translatable("hexcasting.iota.hexcasting:entity").append(Text.translatable("hexlink.or")).append(Text.translatable("hexcasting.iota.hexcasting:vec3")))
    }
    return source
}

fun List<Iota>.getSpiritSource(ctx: CastingEnvironment, index: Int, max: Int): SpiritSource
    = getSpiritSourceOpt(ctx, index, max) ?: throw InvalidSpiritSource(get(index))


fun List<Iota>.getSpiritSourceAndPos(ctx: CastingEnvironment, index: Int, max: Int): Pair<SpiritSource,Vec3d>
    = makePosPair(getSpiritSource(ctx,index,max), index)


fun List<Iota>.getSpiritTarget(ctx: CastingEnvironment, index: Int, max: Int): SpiritTarget{
    val target=when(val targetIota=get(index)){
        is EntityIota ->{
            ctx.assertEntityInRange(targetIota.entity)
            SpiritHelper.spiritTarget(ctx.caster,targetIota.entity)
        }
        is Vec3Iota ->{
            ctx.assertVecInRange(targetIota.vec3)
            SpiritHelper.spiritTarget(ctx.caster, ctx.world, targetIota.vec3)
        }
        else -> throw MishapInvalidIota(get(index), max-index+1, Text.translatable("hexcasting.iota.hexcasting:entity").append(Text.translatable("hexlink.or")).append(Text.translatable("hexcasting.iota.hexcasting:vec3")))
    }
    return target ?: throw InvalidSpiritTarget(get(index))
}

fun List<Iota>.getSpiritTargetAndPos(ctx: CastingEnvironment, index: Int, max: Int): Pair<SpiritTarget,Vec3d>
        = makePosPair(getSpiritTarget(ctx,index,max), index)

fun List<Iota>.getExtractorItem(ctx: CastingEnvironment, index: Int, max: Int): SpiritExtractor<*>{
    val source=when(val iota=get(index)){
        is EntityIota ->{
            ctx.assertEntityInRange(iota.entity)
            val stack=StackHelper.stack(ctx.caster, iota.entity)?.stack
            val item=stack?.item
            val extractor=if(item is ExtractorItem) item.getExtractor(stack) else null
            extractor ?: throw MishapBadEntity(iota.entity, Text.translatable("hexlink.mishap.extractor_item"))
        }
        is Vec3Iota ->{
            ctx.assertVecInRange(iota.vec3)
            val stack=StackHelper.stack(ctx.caster, ctx.world, iota.vec3)?.stack
            val item=stack?.item
            val extractor=if(item is ExtractorItem) item.getExtractor(stack) else null
            extractor ?: throw MishapBadBlock(BlockPos.ofFloored(iota.vec3), Text.translatable("hexlink.mishap.extractor_item"))
        }
        else -> throw MishapInvalidIota(get(index), max-index+1, Text.translatable("hexcasting.iota.hexcasting:entity").append(Text.translatable("hexlink.or")).append(Text.translatable("hexcasting.iota.hexcasting:vec3")))
    }
    return source
}


fun List<Iota>.getVec3orEntity(ctx: CastingEnvironment, index: Int, max: Int): Any{
    return when(val iota=get(index)){
        is EntityIota ->{
            ctx.assertEntityInRange(iota.entity)
            iota.entity
        }
        is Vec3Iota ->{
            ctx.assertVecInRange(iota.vec3)
            iota.vec3
        }
        else -> throw MishapInvalidIota(get(index), max-index+1, Text.translatable("hexcasting.iota.hexcasting:entity").append(Text.translatable("hexlink.or")).append(Text.translatable("hexcasting.iota.hexcasting:vec3")))
    }
}

fun List<Iota>.getExtractorItemAndPos(ctx: CastingEnvironment, index: Int, max: Int): Pair<SpiritExtractor<*>,Vec3d>
    = makePosPair(getExtractorItem(ctx,index,max),index)