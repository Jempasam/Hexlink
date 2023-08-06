package jempasam.hexlink.iota.spiritual

import at.petrak.hexcasting.api.spell.iota.EntityIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.IotaType
import at.petrak.hexcasting.api.spell.iota.NullIota
import com.google.common.base.Predicates
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.Registry

class EntitySpiritIota(val entitytype: EntityType<*>): SpiritIota(TYPE, entitytype) {

    fun getEntityType(): EntityType<*> = payload as EntityType<*>

    override fun toleratesOther(that: Iota): Boolean = that is EntitySpiritIota && that.getEntityType()==getEntityType()

    override fun isTruthy(): Boolean = getEntityType().isSummonable

    override fun serialize(): NbtElement {
        return NbtString.of(Registry.ENTITY_TYPE.getId(getEntityType()).toString())
    }

    companion object{
        val TYPE=object : IotaType<EntitySpiritIota>(){
            override fun color(): Int = 0xF1F400

            override fun deserialize(tag: NbtElement, world: ServerWorld): EntitySpiritIota {
                if(tag is NbtString){
                    val type=Registry.ENTITY_TYPE.getOrEmpty(Identifier(tag.asString())).orElseThrow(::IllegalArgumentException)
                    return EntitySpiritIota(type)
                }
                else throw IllegalArgumentException()
            }

            override fun display(tag: NbtElement): Text {
                if(tag is NbtString){
                    val type=Registry.ENTITY_TYPE.getOrEmpty(Identifier(tag.asString()))
                    if(!type.isEmpty){
                        return type.get().getName().copy().append(Text.translatable("hexlink.spirit"))
                    }
                }
                return Text.of("Invalid Block Spirit")
            }
        }
    }

    override fun canDrop(cast: PlayerEntity, world: ServerWorld, position: Vec3d, power: Int): Int {
        return if(getEntityType().isSummonable) 10 else SpiritIota.CANNOT_DO
    }

    override fun drop(cast: PlayerEntity, world: ServerWorld, position: Vec3d, power: Int): Iota {
        val summoned=getEntityType().create(world)
        if(summoned!=null){
            summoned.setPosition(position)
            world.spawnEntity(summoned)
            return EntityIota(summoned)
        }
        else return NullIota()
    }

    override fun canInfuse(cast: PlayerEntity, world: ServerWorld, entity: Entity, power: Int): Int {
        return canDrop(cast, world, entity.pos, power)
    }

    override fun infuse(cast: PlayerEntity, world: ServerWorld, entity: Entity, power: Int) {
        val summoned=getEntityType().create(world)
        if(summoned!=null){
            summoned.setPosition(entity.pos)
            summoned.velocity=entity.velocity
            world.spawnEntity(summoned)
            entity.startRiding(summoned)
        }
    }

    override fun testEntity(cast: PlayerEntity, world: ServerWorld, entity: Entity): Boolean {
        return entity.type==getEntityType()
    }

    override fun testPos(cast: PlayerEntity, world: ServerWorld, position: Vec3d): Boolean {
        val entities=world.getEntitiesByType( getEntityType(), Box.of(position, 0.7, 0.7, 0.7), Predicates.alwaysTrue())
        return !entities.isEmpty()
    }

}