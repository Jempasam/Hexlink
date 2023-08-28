package jempasam.hexlink.spirit

import com.google.common.base.Predicates
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
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

    override fun manifestAt(caster: PlayerEntity, world: ServerWorld, position: Vec3d, count: Int): Spirit.Manifestation {
        if(!entity_type.isSummonable)
            return Spirit.NONE_MANIFESTATION
        else
            return Spirit.Manifestation(1,count){
                val summoned=entity_type.create(world)
                if(summoned!=null){
                    for(i in 0..<it){
                        summoned.setPosition(position)
                        world.spawnEntity(summoned)
                    }
                }
            }
    }

    override fun manifestIn(caster: PlayerEntity, world: ServerWorld, entity: Entity, count: Int): Spirit.Manifestation {
        if(!entity_type.isSummonable)
            return Spirit.NONE_MANIFESTATION
        else
            return Spirit.Manifestation(1,count){
                val summoned=entity_type.create(world)
                if(summoned!=null){
                    var riding=entity
                    for(i in 0..<it){
                        summoned.setPosition(riding.pos)
                        summoned.velocity=riding.velocity
                        world.spawnEntity(summoned)
                        riding.startRiding(summoned)
                        riding=summoned
                    }
                }
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

    override fun hashCode(): Int = entity_type.hashCode()*36



    override fun getColor(): Int{
        if(entity_type.isFireImmune)return DyeColor.ORANGE.fireworkColor
        else{
            val color= from_group_to_color[entity_type.spawnGroup]
            if(color!=null)return color
            else return DyeColor.RED.fireworkColor
        }
    }

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

    companion object{
        private val from_group_to_color= mapOf(
                SpawnGroup.MONSTER to DyeColor.RED.fireworkColor,
                SpawnGroup.CREATURE to DyeColor.LIME.fireworkColor,
                SpawnGroup.AMBIENT to DyeColor.WHITE.fireworkColor,
                SpawnGroup.AXOLOTLS to DyeColor.PINK.fireworkColor,
                SpawnGroup.UNDERGROUND_WATER_CREATURE to DyeColor.BLUE.fireworkColor,
                SpawnGroup.WATER_CREATURE to DyeColor.LIGHT_BLUE.fireworkColor,
                SpawnGroup.WATER_AMBIENT to DyeColor.LIGHT_BLUE.fireworkColor,
                SpawnGroup.MISC to DyeColor.PURPLE.fireworkColor
        )
    }
}