package jempasam.hexlink.operators.spells

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.iota.Iota
import jempasam.hexlink.block.HexlinkBlocks
import jempasam.hexlink.mishap.MishapCantMixAt
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

//TODO Test Empty vortex summoning
class OpVortexSpirit : SpellAction {
    override val argc: Int
        get() = 1

    override fun execute(args: List<Iota>, ctx: CastingEnvironment): SpellAction.Result {
        val pos=args.getBlockPos(0,1)
        ctx.assertPosInRange(pos)
        if(HexlinkBlocks.VORTEX.canAddAt(ctx.world,pos)){
            return SpellAction.Result(
                    Spell(ctx.world, pos),
                    10,
                    listOf(ParticleSpray.burst(Vec3d.ofCenter(pos),0.5,6))
            )
        }
        else throw MishapCantMixAt()
    }

    class Spell(val world: ServerWorld, val pos: BlockPos): RenderedSpell{
        override fun cast(ctx: CastingEnvironment) {
            HexlinkBlocks.VORTEX.addAt(world, pos)
        }
    }
}