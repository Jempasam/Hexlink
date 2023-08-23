package jempasam.hexlink.operators.spells

import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getBlockPos
import at.petrak.hexcasting.api.spell.iota.Iota
import jempasam.hexlink.block.HexlinkBlocks
import jempasam.hexlink.mishap.MishapCantMixAt
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

//TODO Test Empty vortex summoning
class OpVortexSpirit : SpellAction {
    override val argc: Int
        get() = 1

    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val pos=args.getBlockPos(0,1)
        ctx.assertVecInRange(pos)
        if(HexlinkBlocks.VORTEX.canAddAt(ctx.world,pos)){
            return Triple(
                    Spell(ctx.world, pos, ctx.caster),
                    10,
                    listOf(ParticleSpray.burst(Vec3d.ofCenter(pos),0.5,6))
            )
        }
        else throw MishapCantMixAt()
    }

    class Spell(val world: ServerWorld, val pos: BlockPos, val caster: PlayerEntity): RenderedSpell{
        override fun cast(ctx: CastingContext) {
            HexlinkBlocks.VORTEX.addAt(world, pos)
        }
    }
}