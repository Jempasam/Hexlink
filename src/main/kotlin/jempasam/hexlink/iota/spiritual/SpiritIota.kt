package jempasam.hexlink.iota.spiritual

import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.IotaType
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Vec3d

abstract class SpiritIota(type: IotaType<*>, payload: Any) : Iota(type, payload) {
    abstract fun drop(cast: PlayerEntity, world: ServerWorld, position: Vec3d, power: Int): Iota
    abstract fun canDrop(cast: PlayerEntity, world: ServerWorld, position: Vec3d, power: Int): Int

    abstract fun infuse(cast: PlayerEntity, world: ServerWorld, entity: Entity, power: Int)
    abstract fun canInfuse(cast: PlayerEntity, world: ServerWorld, entity: Entity, power: Int): Int

    abstract fun testPos(cast: PlayerEntity, world: ServerWorld, position: Vec3d): Boolean
    abstract fun testEntity(cast: PlayerEntity, world: ServerWorld, entity: Entity): Boolean

    companion object {
        const val CANNOT_DO: Int = -1
    }
}