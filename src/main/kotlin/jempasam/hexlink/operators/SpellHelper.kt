package jempasam.hexlink.operators

import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.EntityIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.Vec3Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapBadBlock
import at.petrak.hexcasting.api.spell.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import jempasam.hexlink.iota.SpiritIota
import jempasam.hexlink.item.functionnality.ExtractorItem
import jempasam.hexlink.mishap.InvalidSpiritSource
import jempasam.hexlink.mishap.InvalidSpiritTarget
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.StackHelper
import jempasam.hexlink.spirit.extracter.SpiritExtractor
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

fun List<Iota>.getSpiritSourceAndPos(ctx: CastingContext, index: Int, max: Int): Pair<SpiritSource,Vec3d>
    = makePosPair(getSpiritSource(ctx,index,max), index)


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

fun List<Iota>.getSpiritTargetAndPos(ctx: CastingContext, index: Int, max: Int): Pair<SpiritTarget,Vec3d>
        = makePosPair(getSpiritTarget(ctx,index,max), index)

fun List<Iota>.getExtractorItem(ctx: CastingContext, index: Int, max: Int): SpiritExtractor<*>{
    val iota=get(index)
    val source=when(iota){
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
            extractor ?: throw MishapBadBlock(BlockPos(iota.vec3), Text.translatable("hexlink.mishap.extractor_item"))
        }
        else -> throw MishapInvalidIota(get(index), max-index+1, Text.translatable("hexcasting.iota.hexcasting:entity").append(Text.translatable("hexlink.or")).append(Text.translatable("hexcasting.iota.hexcasting:vec3")))
    }
    return source
}

fun List<Iota>.getExtractorItemAndPos(ctx: CastingContext, index: Int, max: Int): Pair<SpiritExtractor<*>,Vec3d>
    = makePosPair(getExtractorItem(ctx,index,max),index)