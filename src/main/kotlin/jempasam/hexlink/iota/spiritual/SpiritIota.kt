package jempasam.hexlink.iota.spiritual

import at.petrak.hexcasting.api.spell.iota.Iota
import net.minecraft.entity.Entity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Vec3d

interface SpiritIota {
    fun drop(world: ServerWorld, position: Vec3d, power: Int): Iota
    fun canDrop(world: ServerWorld, position: Vec3d, power: Int): Int

    fun infuse(world: ServerWorld, entity: Entity, power: Int)
    fun canInfuse(world: ServerWorld, entity: Entity, power: Int): Int

    fun testPos(world: ServerWorld, position: Vec3d): Boolean
    fun testEntity(world: ServerWorld, entity: Entity): Boolean

    companion object {
        public const val CANNOT_DO: Int = -1
    }
}