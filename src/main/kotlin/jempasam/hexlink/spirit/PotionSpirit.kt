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

class PotionSpirit(val potion_effect: StatusEffect): Spirit  {

    override fun infuseAtCost(caster: PlayerEntity, world: ServerWorld, position: Vec3d, power: Int): Int {
        return 5*power*power
    }

    override fun infuseAt(caster: PlayerEntity, world: ServerWorld, position: Vec3d, power: Int) {
        val effect=StatusEffectInstance(potion_effect, power*100, power/4)
        val cloud=EntityType.AREA_EFFECT_CLOUD.create(world)
        if(cloud!=null){
            cloud.addEffect(effect)
            cloud.color=potion_effect.color
            cloud.radius=power.toFloat()
            cloud.duration=power*100
            cloud.setPosition(position)
            world.spawnEntity(cloud)
        }
    }



    override fun infuseInCost(caster: PlayerEntity, world: ServerWorld, entity: Entity, power: Int): Int {
        if(entity is LivingEntity)return 5*power*power
        else return Spirit.CANNOT_USE
    }

    override fun infuseIn(caster: PlayerEntity, world: ServerWorld, entity: Entity, power: Int) {
        val effect=StatusEffectInstance(potion_effect, power*200, power/3)
        if(entity is LivingEntity)entity.addStatusEffect(effect)
    }



    override fun lookIn(caster: PlayerEntity, world: ServerWorld, entity: Entity): Boolean {
        return (entity is LivingEntity && entity.statusEffects.any { effect -> effect.effectType==potion_effect })
    }

    override fun lookAt(caster: PlayerEntity, world: ServerWorld, position: Vec3d): Boolean {
        return false
    }



    override fun equals(other: Any?): Boolean = other is PotionSpirit && potion_effect===other.potion_effect



    override fun getColor(): Int = potion_effect.color

    override fun getName(): Text = potion_effect.name

    override fun serialize(): NbtElement {
        return NbtString.of(Registry.STATUS_EFFECT.getId(potion_effect).toString())
    }



    override fun getType(): Spirit.SpiritType<*> = Type

    object Type: Spirit.SpiritType<PotionSpirit>{
        override fun getName(): Text {
            return Text.translatable("hexlink.spirit.potion")
        }

        override fun deserialize(nbt: NbtElement): PotionSpirit {
            if(nbt is NbtString){
                val type=Registry.STATUS_EFFECT.getOrEmpty(Identifier(nbt.asString())).orElseThrow(::IllegalArgumentException)
                return PotionSpirit(type)
            }
            else throw IllegalArgumentException()
        }
    }





}