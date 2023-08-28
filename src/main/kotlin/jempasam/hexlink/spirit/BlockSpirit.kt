package jempasam.hexlink.spirit

import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.Registry

//TODO Long time block interaction with manifestation
class BlockSpirit(val block: Block): Spirit{

    override fun manifestAt(caster: PlayerEntity, world: ServerWorld, position: Vec3d, power: Int): Spirit.Manifestation {
        val startpos=BlockPos(position)
        var testpos=startpos
        var final_power=0
        while(final_power<power){
            if(!world.getBlockState(testpos).isAir) break
            testpos=testpos.up()
            final_power++
        }
        if(final_power==0)return Spirit.NONE_MANIFESTATION

        return Spirit.Manifestation(3, final_power){
            var blockpos=startpos
            for(i in 0..<it){
                world.setBlockState(blockpos, block.defaultState)
                blockpos=blockpos.up()
            }
        }
    }

    override fun manifestIn(caster: PlayerEntity, world: ServerWorld, entity: Entity, count: Int): Spirit.Manifestation {
        return Spirit.Manifestation(1,1){
            val pos=BlockPos(entity.pos)
            val state=block.defaultState
            block.onEntityCollision(state, world, pos, entity)
            block.onSteppedOn(world, pos, state, entity)
            if(entity is PlayerEntity){
                block.onUse(state, world, pos, entity, Hand.MAIN_HAND, BlockHitResult(entity.pos, Direction.UP, BlockPos(entity.pos), true))
            }
        }
    }



    override fun lookAt(caster: PlayerEntity, world: ServerWorld, position: Vec3d): Boolean {
        return world.getBlockState(BlockPos(position)).block==block
    }

    override fun lookIn(caster: PlayerEntity, world: ServerWorld, entity: Entity): Boolean {
        return lookAt(caster, world, entity.pos.add(0.0, -0.5, -0.0))
    }



    override fun equals(other: Any?): Boolean = other is BlockSpirit && block===other.block

    override fun hashCode(): Int = block.hashCode()*36



    override fun getColor(): Int = block.defaultMapColor.color

    override fun getName(): Text = block.name

    override fun serialize(): NbtElement {
        return NbtString.of(Registry.BLOCK.getId(block).toString())
    }



    override fun getType(): Spirit.SpiritType<*> = Type

    object Type: Spirit.SpiritType<BlockSpirit>{
        override fun getName(): Text {
            return Text.translatable("hexlink.spirit.block")
        }

        override fun deserialize(nbt: NbtElement): BlockSpirit {
            if(nbt is NbtString){
                val type=Registry.BLOCK.getOrEmpty(Identifier(nbt.asString())).orElseThrow(::IllegalArgumentException)
                return BlockSpirit(type)
            }
            else throw IllegalArgumentException()
        }
    }

}