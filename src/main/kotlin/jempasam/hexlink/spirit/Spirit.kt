package jempasam.hexlink.spirit

import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtElement
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d

/**
 * A Spirit Type
 */
interface Spirit {

    /**
     * Get media cost of infusing at a location
     * @param position Infusion location
     * @param power Infusion strength
     * @return CANNOT_USE Infusion is not possible
     * @return Infusion media cost
     */
    fun infuseAtCost(caster: PlayerEntity, world: ServerWorld, position: Vec3d, power: Int): Int

    /**
     * Infuse the spirit at a location
     */
    fun infuseAt(caster: PlayerEntity, world: ServerWorld, position: Vec3d, power: Int)


    /**
     * Get media cost of infusing in an entity
     * @param entity Infusion location
     * @param power Infusion strength
     * @return CANNOT_USE Infusion is not possible
     * @return Infusion media cost
     */
    fun infuseInCost(caster: PlayerEntity, world: ServerWorld, entity: Entity, power: Int): Int

    /**
     * Infuse the spirit in an entity
     */
    fun infuseIn(caster: PlayerEntity, world: ServerWorld, entity: Entity, power: Int)


    /**
     * Make the entity look at a location
     */
    fun lookAt(caster: PlayerEntity, world: ServerWorld, position: Vec3d): Boolean

    /**
     * Make the entity look at an entity
     */
    fun lookIn(caster: PlayerEntity, world: ServerWorld, entity: Entity): Boolean

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

    /**
     * Get spirit color
     */
    fun getColor(): Int

    /**
     * Get spirit name
     */
    fun getName(): Text

    fun serialize(): NbtElement

    fun getType(): SpiritType<*>


    interface SpiritType<T: Spirit>{
        fun deserialize(nbt: NbtElement): T?

        fun getName(): Text

    }


    companion object{
        const val CANNOT_USE=-1
    }
}