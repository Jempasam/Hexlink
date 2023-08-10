package jempasam.hexlink.operators


import at.petrak.hexcasting.api.spell.Action
import at.petrak.hexcasting.api.spell.OperationResult
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.Vec3Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.spell.mishaps.MishapLocationTooFarAway
import at.petrak.hexcasting.api.spell.mishaps.MishapNotEnoughArgs
import hexlink.iota.BlockTypeIota
import net.minecraft.util.math.BlockPos

class GetBlockType : Action{

    override fun operate(continuation: SpellContinuation, stack: MutableList<Iota>, ravenmind: Iota?, ctx: CastingContext): OperationResult{
        if(stack.size<1)throw MishapNotEnoughArgs(1, stack.size)

        var last=stack.last()
        if(last is Vec3Iota){
            stack.removeLast()
            var blocktype=ctx.world.getBlockState(BlockPos(last.getVec3())).block
            if(blocktype==null)throw MishapLocationTooFarAway(last.getVec3(), "outofbound")
            else stack.add(BlockTypeIota(blocktype))
            return OperationResult(continuation, stack, ravenmind, emptyList())
        }
        else throw MishapInvalidIota(last, 0, Vec3Iota.TYPE.typeName())
    }
}
