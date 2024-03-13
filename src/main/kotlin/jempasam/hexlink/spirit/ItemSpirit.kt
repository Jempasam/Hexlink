package jempasam.hexlink.spirit

import com.google.common.base.Predicates
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.*
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.registry.Registry
import kotlin.jvm.optionals.getOrNull

class ItemSpirit(val item: Item): Spirit {

    private var color: Int=0
    init{
        if(item.isFireproof)
            color=DyeColor.ORANGE.fireworkColor
        else if(item.isFood)
            color=DyeColor.BROWN.fireworkColor
        else if(item.isEnchantable(item.defaultStack))
            color=DyeColor.YELLOW.fireworkColor
        else if(item.hasGlint(item.defaultStack))
            color=DyeColor.MAGENTA.fireworkColor
        else if(item is BlockItem)
            color=item.block.defaultMapColor.color
        else if(item.getRarity(item.defaultStack)!=Rarity.COMMON)
            color=item.getRarity(item.defaultStack).formatting.colorValue ?: DyeColor.ORANGE.signColor
        else
            color=DyeColor.ORANGE.signColor
    }


    override fun manifestAt(caster: PlayerEntity, world: ServerWorld, position: Vec3d, count: Int): Spirit.Manifestation {
        return Spirit.Manifestation(1,count){
            val stack=item.defaultStack
            stack.count=it
            val oldHandItem=caster.mainHandStack
            caster.setStackInHand(Hand.MAIN_HAND,stack)
            for(i in 0..<it){
                item.useOnBlock(
                        ItemUsageContext(
                                caster,
                                Hand.MAIN_HAND,
                                BlockHitResult(position, Direction.UP, BlockPos(position), true)
                        )
                )
            }
            caster.setStackInHand(Hand.MAIN_HAND, oldHandItem)
        }
    }

    override fun manifestIn(caster: PlayerEntity, world: ServerWorld, entity: Entity, count: Int): Spirit.Manifestation {
        return Spirit.Manifestation(1,count){
            val stack=item.defaultStack
            stack.count=it
            val oldHandItem=caster.mainHandStack
            caster.setStackInHand(Hand.MAIN_HAND,stack)
            if(entity==caster){
                for(i in 0..<it){
                    val action=item.getUseAction(stack)
                    val success=item.use(world, caster, Hand.MAIN_HAND)
                    if(action!=UseAction.NONE && action!=UseAction.BLOCK && success.result.isAccepted){
                        item.onStoppedUsing(stack, world, caster, 0)
                        item.finishUsing(stack,world,caster)
                    }
                }
            }
            else{
                for(i in 0..<it) {
                    if (entity is LivingEntity) {
                        item.useOnEntity(stack, caster, entity, Hand.MAIN_HAND)
                    }
                    entity.interact(caster, Hand.MAIN_HAND)
                }
            }
            caster.setStackInHand(Hand.MAIN_HAND, oldHandItem)
        }
    }



    override fun lookIn(caster: PlayerEntity, world: ServerWorld, entity: Entity): Boolean {
        return entity is LivingEntity && (entity.getStackInHand(Hand.MAIN_HAND).item==item) && entity.getStackInHand(Hand.OFF_HAND).item==item
    }

    override fun lookAt(caster: PlayerEntity, world: ServerWorld, position: Vec3d): Boolean {
        val entities=world.getEntitiesByType( EntityType.ITEM, Box.of(position, 0.7, 0.7, 0.7), Predicates.alwaysTrue())
        return entities.any { entity -> entity.stack.item==item }
    }



    override fun equals(other: Any?): Boolean = other is ItemSpirit && item===other.item

    override fun hashCode(): Int = item.hashCode()*36

    override fun getColor(): Int{
        return color
    }

    override fun getName(): Text = item.name

    override fun serialize(): NbtElement {
        return NbtString.of(Registries.ITEM.getId(item).toString())
    }



    override fun getType(): Spirit.SpiritType<*> = Type

    object Type: Spirit.SpiritType<ItemSpirit>{
        override fun getName(): Text {
            return Text.translatable("hexlink.spirit.item")
        }

        override fun deserialize(nbt: NbtElement): ItemSpirit? {
            if(nbt is NbtString){
                val type=Registries.ITEM.getOrEmpty(Identifier(nbt.asString())).getOrNull()
                return if(type!=null) ItemSpirit(type) else null
            }
            else throw IllegalArgumentException()
        }
    }



}