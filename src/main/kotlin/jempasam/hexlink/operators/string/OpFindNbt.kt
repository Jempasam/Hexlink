package jempasam.hexlink.operators.string

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getList
import at.petrak.hexcasting.api.spell.iota.DoubleIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.NullIota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import jempasam.hexlink.operators.getVec3orEntity
import jempasam.hexlink.utils.NbtToIotaHelper.toIota
import net.minecraft.entity.Entity
import net.minecraft.nbt.AbstractNbtList
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import ram.talia.moreiotas.api.spell.iota.StringIota
import ram.talia.moreiotas.common.lib.MoreIotasIotaTypes

object OpFindNbt : ConstMediaAction {

    override val argc: Int = 2

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        // Get and check parameters
        val target=args.getVec3orEntity(ctx, 0, argc)
        val path_list=args.getList(1, argc)

        val nbt=when(target){
            is Vec3d -> ctx .world .getBlockEntity(BlockPos(target)) ?.createNbt()
            is Entity -> target.writeNbt(NbtCompound())
            else -> null
        }

        for(part in path_list){
            if(part.type!==HexIotaTypes.DOUBLE && part.type!==MoreIotasIotaTypes.STRING_TYPE){
                throw MishapInvalidIota(args.get(1), 0, Text.of("Expected a list of string or number"))
            }
        }

        // Traverse the NBT
        var currentNbt: NbtElement?=nbt
        for(key in path_list){
            println(currentNbt)
            if(currentNbt==null)break
            if(key is StringIota){
                if(currentNbt is NbtCompound)currentNbt= currentNbt.get(key.string)
                else currentNbt= null
            }
            else if(key is DoubleIota){
                if(currentNbt is AbstractNbtList<*>){
                    var index= key.double.toInt()
                    if(index<0)index= currentNbt.size-index
                    currentNbt= currentNbt.getOrNull(index)
                }
                else currentNbt= null
            }
        }

        // Return the nbt
        val iota=currentNbt?.toIota() ?: NullIota()

        return listOf(iota)
    }

}