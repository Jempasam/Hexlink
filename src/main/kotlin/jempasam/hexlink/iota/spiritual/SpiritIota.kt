package jempasam.hexlink.iota.spiritual

import at.petrak.hexcasting.api.spell.iota.Iota
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Vec3d

interface SpiritIota {
    fun drop(cast: PlayerEntity, world: ServerWorld, position: Vec3d, power: Int): Iota
    fun canDrop(cast: PlayerEntity, world: ServerWorld, position: Vec3d, power: Int): Int

    fun infuse(cast: PlayerEntity, world: ServerWorld, entity: Entity, power: Int)
    fun canInfuse(cast: PlayerEntity, world: ServerWorld, entity: Entity, power: Int): Int

    fun testPos(cast: PlayerEntity, world: ServerWorld, position: Vec3d): Boolean
    fun testEntity(cast: PlayerEntity, world: ServerWorld, entity: Entity): Boolean

    companion object {
        const val CANNOT_DO: Int = -1
    }
}