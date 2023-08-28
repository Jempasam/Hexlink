package jempasam.hexlink.spirit

import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.utils.asNBT
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
     * Manifest the spirit at a location.
     * @param caster The caster of the spell
     * @param world The world where the spell is cast
     * @param position The location
     * @param count Spirit count used in the infusion
     */
    fun manifestAt(caster: PlayerEntity, world: ServerWorld, position: Vec3d, count: Int): Manifestation

    /**
     * Manifest the spirit in an entity.
     * @param caster The caster of the spell
     * @param world The world where the spell is cast
     * @param entity The entity
     * @param count Spirit count used in the infusion
     */
    fun manifestIn(caster: PlayerEntity, world: ServerWorld, entity: Entity, count: Int): Manifestation

    class Manifestation(val perSpiritCost: Int, val spiritCount: Int, private val action: (Int)->Unit){
        fun execute(count: Int){
            if(count>spiritCount){
                HexlinkMod.logger.warn("Try to manifest more spirit than the Manifestation maximum")
                action(spiritCount)
            }
            else action(count)
        }

        fun mediaCost(count: Int) = count*perSpiritCost

        val maxMediaCost: Int get()=perSpiritCost*spiritCount
    }


    /**
     * Make the spirit look at a location
     */
    fun lookAt(caster: PlayerEntity, world: ServerWorld, position: Vec3d): Boolean

    /**
     * Make the spirit look at an entity
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

        fun deserialize(obj: JsonObject): T{
            val element=obj.get("value")
            if(element==null)throw JsonSyntaxException("Expected \"value\"")
            val ret=deserialize(element.asNBT())
            if(ret==null)throw JsonSyntaxException("Invalid json")
            return ret
        }

    }


    companion object{
        const val CANNOT_USE=-1
        val NONE_MANIFESTATION=Manifestation(0,0){}
    }
}