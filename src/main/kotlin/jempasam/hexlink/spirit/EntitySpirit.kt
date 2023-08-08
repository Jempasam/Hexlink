package jempasam.hexlink.spirit

import at.petrak.hexcasting.api.spell.iota.EntityIota
import com.google.common.base.Predicates
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.Registry

class EntitySpirit(val entity_type: EntityType<*>): Spirit {

    override fun infuseAtCost(caster: PlayerEntity, world: ServerWorld, position: Vec3d, power: Int): Int {
        return if(entity_type.isSummonable) 10 else Spirit.CANNOT_USE
    }

    override fun infuseAt(caster: PlayerEntity, world: ServerWorld, position: Vec3d, power: Int) {
        val summoned=entity_type.create(world)
        if(summoned!=null){
            summoned.setPosition(position)
            world.spawnEntity(summoned)
            EntityIota(summoned)
        }
    }



    override fun infuseInCost(caster: PlayerEntity, world: ServerWorld, entity: Entity, power: Int): Int {
        return infuseAtCost(caster, world, entity.pos, power)
    }

    override fun infuseIn(caster: PlayerEntity, world: ServerWorld, entity: Entity, power: Int) {
        val summoned=entity_type.create(world)
        if(summoned!=null){
            summoned.setPosition(entity.pos)
            summoned.velocity=entity.velocity
            world.spawnEntity(summoned)
            entity.startRiding(summoned)
        }
    }



    override fun lookIn(caster: PlayerEntity, world: ServerWorld, entity: Entity): Boolean {
        return entity.type==entity_type
    }

    override fun lookAt(caster: PlayerEntity, world: ServerWorld, position: Vec3d): Boolean {
        val entities=world.getEntitiesByType( entity_type, Box.of(position, 0.7, 0.7, 0.7), Predicates.alwaysTrue())
        return entities.isNotEmpty()
    }



    override fun equals(other: Any?): Boolean = other is EntitySpirit && entity_type===other.entity_type



    override fun getColor(): Int = DyeColor.MAGENTA.signColor

    override fun getName(): Text = entity_type.name

    override fun serialize(): NbtElement {
        return NbtString.of(Registry.ENTITY_TYPE.getId(entity_type).toString())
    }



    override fun getType(): Spirit.SpiritType<*> = Type

    object Type: Spirit.SpiritType<EntitySpirit>{
        override fun getName(): Text {
            return Text.translatable("hexlink.spirit.entity")
        }

        override fun deserialize(nbt: NbtElement): EntitySpirit {
            if(nbt is NbtString){
                val type=Registry.ENTITY_TYPE.getOrEmpty(Identifier(nbt.asString())).orElseThrow(::IllegalArgumentException)
                return EntitySpirit(type)
            }
            else throw IllegalArgumentException()
        }
    }
}