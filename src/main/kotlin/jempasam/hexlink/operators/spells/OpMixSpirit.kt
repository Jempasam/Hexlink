package jempasam.hexlink.operators.spells

import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getBlockPos
import at.petrak.hexcasting.api.spell.iota.Iota
import jempasam.hexlink.block.HexlinkBlocks
import jempasam.hexlink.item.SoulContainerItem
import jempasam.hexlink.mishap.MishapCantMixAt
import jempasam.hexlink.mishap.MishapNoEnoughSoul
import jempasam.hexlink.operators.getSpirit
import jempasam.hexlink.spirit.Spirit
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

class OpMixSpirit : SpellAction {
    override val argc: Int
        get() = 2

    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val spirit=args.getSpirit(0, 2)
        val pos=args.getBlockPos(1,2)
        ctx.assertVecInRange(pos)
        if(HexlinkBlocks.VORTEX.canAddAt(ctx.world,pos)){
            val soul_source=SoulContainerItem.getSpiritConsumable(ctx.caster.isCreative, ctx.caster.inventory, spirit)
            if(soul_source==null)throw MishapNoEnoughSoul(spirit,1)
            return Triple(
                    Spell(ctx.world, pos, spirit, soul_source, ctx.caster),
                    10,
                    listOf(ParticleSpray.burst(Vec3d.ofCenter(pos),0.5,6))
            )
        }
        else throw MishapCantMixAt()
    }

    class Spell(val world: ServerWorld, val pos: BlockPos, val spirit: Spirit, val soul_source: SoulContainerItem.SoulSource, val caster: PlayerEntity): RenderedSpell{
        override fun cast(ctx: CastingContext) {
            HexlinkBlocks.VORTEX.addAt(world, pos, spirit)
            soul_source.consume()
        }
    }
}