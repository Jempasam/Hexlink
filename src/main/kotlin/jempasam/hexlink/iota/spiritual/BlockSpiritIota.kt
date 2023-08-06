package jempasam.hexlink.iota.spiritual

import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.IotaType
import at.petrak.hexcasting.api.spell.iota.Vec3Iota
import hexlink.iota.BlockTypeIota
import net.minecraft.block.Block
import net.minecraft.block.Blocks
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

class BlockSpiritIota(blocktype: Block): SpiritIota(TYPE, blocktype) {

    fun getBlockType(): Block = payload as Block

    override fun toleratesOther(that: Iota): Boolean = that is BlockTypeIota && that.getBlockType()==getBlockType()

    override fun isTruthy(): Boolean = getBlockType()!= Blocks.AIR

    override fun serialize(): NbtElement {
        return NbtString.of(Registry.BLOCK.getId(getBlockType()).toString())
    }

    companion object{
        val TYPE=object : IotaType<BlockSpiritIota>(){
            override fun color(): Int = 0xF1F400

            override fun deserialize(tag: NbtElement, world: ServerWorld): BlockSpiritIota {
                if(tag is NbtString){
                    val type=Registry.BLOCK.getOrEmpty(Identifier(tag.asString())).orElseThrow(::IllegalArgumentException)
                    return BlockSpiritIota(type)
                }
                else throw IllegalArgumentException()
            }

            override fun display(tag: NbtElement): Text {
                if(tag is NbtString){
                    val type=Registry.BLOCK.getOrEmpty(Identifier(tag.asString()))
                    if(!type.isEmpty){
                        return type.get().name.append(Text.translatable("hexlink.spirit"))
                    }
                }
                return Text.of("Invalid Block Spirit")
            }
        }
    }


    override fun canDrop(cast: PlayerEntity, world: ServerWorld, position: Vec3d, power: Int): Int {
        return if(world.getBlockState(BlockPos(position)).isAir)  10 else SpiritIota.CANNOT_DO
    }

    override fun drop(cast: PlayerEntity, world: ServerWorld, position: Vec3d, power: Int): Iota {
        val blockpos=BlockPos(position)
        world.setBlockState(blockpos,getBlockType().defaultState)
        return Vec3Iota(Vec3d.of(blockpos).add(Vec3d(0.5,0.5,0.5)))
    }

    override fun canInfuse(cast: PlayerEntity, world: ServerWorld, entity: Entity, power: Int): Int {
       return 2
    }

    override fun infuse(cast: PlayerEntity, world: ServerWorld, entity: Entity, power: Int) {
        val pos=BlockPos(entity.pos)
        val state=getBlockType().defaultState
        getBlockType().onEntityCollision(state, world, pos, entity)
        getBlockType().onSteppedOn(world, pos, state, entity)
        if(entity is PlayerEntity){
            getBlockType().onUse(state, world, pos, entity, Hand.MAIN_HAND, BlockHitResult(entity.pos, Direction.UP, BlockPos(entity.pos), true))
        }
    }

    override fun testPos(cast: PlayerEntity, world: ServerWorld, position: Vec3d): Boolean {
        return world.getBlockState(BlockPos(position)).block==getBlockType()
    }

    override fun testEntity(cast: PlayerEntity, world: ServerWorld, entity: Entity): Boolean {
        return testPos(cast, world, entity.pos.add(0.0, -0.5, -0.0))
    }

}