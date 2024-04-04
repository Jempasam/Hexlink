package jempasam.hexlink.spirit

import com.google.common.base.Predicates
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.SpawnGroup
import net.minecraft.item.SpawnEggItem
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString
import net.minecraft.registry.Registries
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import net.minecraft.util.math.Box
import net.minecraft.util.math.ColorHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import kotlin.jvm.optionals.getOrNull
import kotlin.math.sin

class EntitySpirit(val entityType: EntityType<*>): Spirit {

    // Spawn At
    fun spawn(world: World, position: Vec3d, count: Int, modifier: (Int, Entity)->Unit): Spirit.Manifestation{
        if(!entityType.isSummonable)
            return Spirit.NONE_MANIFESTATION
        else
            return Spirit.Manifestation(1,count){
                for(i in 0..<it){
                    val summoned = entityType.create(world)
                    if(summoned!=null) {
                        summoned.setPosition(position)
                        modifier(i, summoned)
                        world.spawnEntity(summoned)
                    }
                }
            }
    }

    override fun manifestAt(caster: LivingEntity?, world: ServerWorld, position: Vec3d, count: Int): Spirit.Manifestation {
        return spawn(world, position, count){_,_->}
    }

    override fun manifestBetween(caster: LivingEntity?, world: ServerWorld, from: Vec3d, to: Vec3d, count: Int): Spirit.Manifestation {
        var direction=to.subtract(from)
        if(direction.length()>5)direction=direction.normalize().multiply(5.0)
        return spawn(world, from, count){ i,e->
            e.setVelocity(direction.x, direction.y+i*0.5, direction.z)
        }
    }

    override fun manifestBetween(caster: LivingEntity?, world: ServerWorld, from: Entity, to: Vec3d, count: Int): Spirit.Manifestation {
        return spawn(world, to, count){ i, e ->
            if(from is LivingEntity && e is LivingEntity)from.attacker=e
        }
    }



    // Spawn Riding
    fun spawn(world: World, rided: Entity, count: Int, modifier: (Entity)->Unit): Spirit.Manifestation{
        if(!entityType.isSummonable)
            return Spirit.NONE_MANIFESTATION
        else
            return Spirit.Manifestation(1,count){
                var riding=rided
                for(i in 0..<it){
                    val summoned = entityType.create(world)
                    if(summoned!=null) {
                        summoned.setPosition(riding.pos)
                        summoned.velocity=riding.velocity
                        world.spawnEntity(summoned)
                        riding.startRiding(summoned)
                        riding=summoned
                    }
                }
                modifier(riding)
            }
    }

    override fun manifestIn(caster: LivingEntity?, world: ServerWorld, entity: Entity, count: Int): Spirit.Manifestation {
        return spawn(world, entity, count){}
    }

    override fun manifestBetween(caster: LivingEntity?, world: ServerWorld, from: Entity, to: Entity, count: Int): Spirit.Manifestation {
        return spawn(world, from, count){
            if(it is LivingEntity && to is LivingEntity)it.attacker=to
        }
    }







    override fun lookIn(caster: LivingEntity?, world: ServerWorld, entity: Entity): Boolean {
        return entity.type==entityType
    }

    override fun lookAt(caster: LivingEntity?, world: ServerWorld, position: Vec3d): Boolean {
        val entities=world.getEntitiesByType( entityType, Box.of(position, 0.7, 0.7, 0.7), Predicates.alwaysTrue())
        return entities.isNotEmpty()
    }



    override fun equals(other: Any?): Boolean = other is EntitySpirit && entityType===other.entityType

    override fun hashCode(): Int = entityType.hashCode()*36



    override fun getColor(): Int{
        val egg=SpawnEggItem.forEntity(entityType)
        if(egg!=null){
            val forward=sin((System.currentTimeMillis()%2000)/2000f*Math.PI)
            val revert=1f-forward
            val tint1=egg.getColor(0)
            val tint2=egg.getColor(1)
            return ColorHelper.Argb.getArgb(
                    255,
                    (ColorHelper.Argb.getRed(tint1)*forward+ColorHelper.Argb.getRed(tint2)*revert).toInt(),
                    (ColorHelper.Argb.getGreen(tint1)*forward+ColorHelper.Argb.getGreen(tint2)*revert).toInt(),
                    (ColorHelper.Argb.getBlue(tint1)*forward+ColorHelper.Argb.getBlue(tint2)*revert).toInt()
            )
        }
        else if(entityType.isFireImmune)return DyeColor.ORANGE.fireworkColor
        else{
            val color= from_group_to_color[entityType.spawnGroup]
            if(color!=null)return color
            else return DyeColor.RED.fireworkColor
        }
    }

    override fun getName(): Text = entityType.name

    override fun serialize(): NbtElement {
        return NbtString.of(Registries.ENTITY_TYPE.getId(entityType).toString())
    }



    override fun getType(): Spirit.SpiritType<*> = Type

    object Type: Spirit.SpiritType<EntitySpirit>{
        override fun getName(): Text {
            return Text.translatable("hexlink.spirit.entity")
        }

        override fun deserialize(nbt: NbtElement): EntitySpirit? {
            if(nbt is NbtString){
                val type=Registries.ENTITY_TYPE.getOrEmpty(Identifier.tryParse(nbt.asString())).getOrNull()
                return type?.let{EntitySpirit(it)}
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