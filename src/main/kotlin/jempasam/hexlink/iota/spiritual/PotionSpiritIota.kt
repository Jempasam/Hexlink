package jempasam.hexlink.iota.spiritual

import at.petrak.hexcasting.api.spell.iota.EntityIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.IotaType
import at.petrak.hexcasting.api.spell.iota.NullIota
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

class PotionSpiritIota(potioneffect: StatusEffect): SpiritIota(TYPE, potioneffect)  {

    fun getEffect(): StatusEffect = payload as StatusEffect

    override fun toleratesOther(that: Iota): Boolean = that is PotionSpiritIota && that.getEffect()==getEffect()

    override fun isTruthy(): Boolean = getEffect().isBeneficial

    override fun serialize(): NbtElement {
        return NbtString.of(Registry.STATUS_EFFECT.getId(getEffect()).toString())
    }

    companion object{
        val TYPE=object : IotaType<PotionSpiritIota>(){
            override fun color(): Int = 0xF1F400

            override fun deserialize(tag: NbtElement, world: ServerWorld): PotionSpiritIota {
                if(tag is NbtString){
                    val type=Registry.STATUS_EFFECT.getOrEmpty(Identifier(tag.asString())).orElseThrow(::IllegalArgumentException)
                    return PotionSpiritIota(type)
                }
                else throw IllegalArgumentException()
            }

            override fun display(tag: NbtElement): Text {
                if(tag is NbtString){
                    val type=Registry.STATUS_EFFECT.getOrEmpty(Identifier(tag.asString()))
                    if(!type.isEmpty){
                        return type.get().name.copy().append(Text.translatable("hexlink.spirit"))
                    }
                }
                return Text.of("Invalid Potion Spirit")
            }
        }
    }

    override fun canInfuse(cast: PlayerEntity, world: ServerWorld, entity: Entity, power: Int): Int {
        if(entity is LivingEntity)return 5*power*power
        else return SpiritIota.CANNOT_DO
    }

    override fun infuse(cast: PlayerEntity, world: ServerWorld, entity: Entity, power: Int) {
        val effect=StatusEffectInstance(getEffect(), power*200, power/3)
        if(entity is LivingEntity)entity.addStatusEffect(effect)
    }

    override fun canDrop(cast: PlayerEntity, world: ServerWorld, position: Vec3d, power: Int): Int {
        return 5*power*power
    }

    override fun drop(cast: PlayerEntity, world: ServerWorld, position: Vec3d, power: Int): Iota {
        val effect=StatusEffectInstance(getEffect(), power*100, power/4)
        val cloud=EntityType.AREA_EFFECT_CLOUD.create(world)
        if(cloud!=null){
            cloud.addEffect(effect)
            cloud.color=getEffect().color
            cloud.radius=power.toFloat()
            cloud.duration=power*100
            cloud.setPosition(position)
            world.spawnEntity(cloud)
            return EntityIota(cloud)
        }
        return NullIota()
    }

    override fun testEntity(cast: PlayerEntity, world: ServerWorld, entity: Entity): Boolean {
        return (entity is LivingEntity && entity.statusEffects.any { effect -> effect.effectType==getEffect() })
    }

    override fun testPos(cast: PlayerEntity, world: ServerWorld, position: Vec3d): Boolean {
        return false
    }




}