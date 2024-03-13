package jempasam.hexlink.operators.string

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.spell.iota.NullIota
import jempasam.hexlink.operators.getVec3orEntity
import jempasam.hexlink.utils.NbtToIotaHelper.toIota
import net.minecraft.entity.Entity
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

object OpGetNbt : ConstMediaAction {

    override val argc: Int = 1

    override fun execute(args: List<Iota>, ctx: CastingEnvironment): List<Iota> {
        val target=args.getVec3orEntity(ctx, 0,1)

        val nbt=when(target){
            is Vec3d -> ctx .world .getBlockEntity(BlockPos(target)) ?.createNbt()
            is Entity -> target.writeNbt(NbtCompound())
            else -> null
        }

        val iota=nbt?.toIota() ?: NullIota()

        return listOf(iota)
    }

}