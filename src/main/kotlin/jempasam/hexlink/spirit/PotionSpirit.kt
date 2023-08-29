package jempasam.hexlink.spirit

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.Registry

class PotionSpirit(val potionEffect: StatusEffect): Spirit  {

    override fun manifestAt(caster: PlayerEntity, world: ServerWorld, position: Vec3d, count: Int): Spirit.Manifestation {
        return Spirit.Manifestation(1,count){
            val effect=StatusEffectInstance(potionEffect, it*100, it/4)
            val cloud=EntityType.AREA_EFFECT_CLOUD.create(world)
            if(cloud!=null){
                cloud.addEffect(effect)
                cloud.color=potionEffect.color
                cloud.radius=it.toFloat()
                cloud.duration=it*100
                cloud.setPosition(position)
                world.spawnEntity(cloud)
            }
        }
    }



    override fun manifestIn(caster: PlayerEntity, world: ServerWorld, entity: Entity, count: Int): Spirit.Manifestation {
        if(entity !is LivingEntity)
            return Spirit.NONE_MANIFESTATION
        else
            return Spirit.Manifestation(1,count){
                val effect=StatusEffectInstance(potionEffect, it*200, it/3)
                entity.addStatusEffect(effect)
            }
    }



    override fun lookIn(caster: PlayerEntity, world: ServerWorld, entity: Entity): Boolean {
        return (entity is LivingEntity && entity.statusEffects.any { effect -> effect.effectType==potionEffect })
    }

    override fun lookAt(caster: PlayerEntity, world: ServerWorld, position: Vec3d): Boolean {
        return false
    }



    override fun equals(other: Any?): Boolean = other is PotionSpirit && potionEffect===other.potionEffect

    override fun hashCode(): Int = potionEffect.hashCode()*36


    override fun getColor(): Int = potionEffect.color

    override fun getName(): Text = potionEffect.name

    override fun serialize(): NbtElement {
        return NbtString.of(Registry.STATUS_EFFECT.getId(potionEffect).toString())
    }



    override fun getType(): Spirit.SpiritType<*> = Type

    object Type: Spirit.SpiritType<PotionSpirit>{
        override fun getName(): Text {
            return Text.translatable("hexlink.spirit.potion")
        }

        override fun deserialize(nbt: NbtElement): PotionSpirit {
            if(nbt is NbtString){
                val type=Registry.STATUS_EFFECT.getOrEmpty(Identifier.tryParse(nbt.asString())).orElseThrow(::IllegalArgumentException)
                return PotionSpirit(type)
            }
            else throw IllegalArgumentException()
        }
    }





}