package jempasam.hexlink.iota.spiritual

import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.IotaType
import at.petrak.hexcasting.api.spell.iota.NullIota
import com.google.common.base.Predicates
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.Items
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.UseAction
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.Registry

class ItemSpiritIota(val item: Item): Iota(TYPE, item), SpiritIota {

    fun getItemType(): Item = payload as Item

    override fun toleratesOther(that: Iota): Boolean = that is ItemSpiritIota && that.getItemType()==getItemType()

    override fun isTruthy(): Boolean = getItemType()!= Items.AIR

    override fun serialize(): NbtElement {
        return NbtString.of(Registry.ITEM.getId(getItemType()).toString())
    }

    companion object{
        val TYPE=object : IotaType<ItemSpiritIota>(){
            override fun color(): Int = 0xF1F400

            override fun deserialize(tag: NbtElement, world: ServerWorld): ItemSpiritIota {
                if(tag is NbtString){
                    val type=Registry.ITEM.getOrEmpty(Identifier(tag.asString())).orElseThrow(::IllegalArgumentException)
                    return ItemSpiritIota(type)
                }
                else throw IllegalArgumentException()
            }

            override fun display(tag: NbtElement): Text {
                if(tag is NbtString){
                    val type=Registry.ITEM.getOrEmpty(Identifier(tag.asString()))
                    if(!type.isEmpty){
                        return type.get().name.copy().append(Text.translatable("hexlink.spirit"))
                    }
                }
                return Text.of("Invalid Block Spirit")
            }
        }
    }

    override fun canInfuse(cast: PlayerEntity, world: ServerWorld, entity: Entity, power: Int): Int {
        return 10
    }

    override fun infuse(cast: PlayerEntity, world: ServerWorld, entity: Entity, power: Int) {
        val stack=getItemType().defaultStack
        stack.count=1
        val old_hand_item=cast.mainHandStack
        cast.setStackInHand(Hand.MAIN_HAND,stack)
        if(entity==cast){
            val action=getItemType().getUseAction(stack)
            val success=getItemType().use(world, cast, Hand.MAIN_HAND)
            if(action!=UseAction.NONE && action!=UseAction.BLOCK && success.result.isAccepted){
                getItemType().onStoppedUsing(stack, world, cast, 0)
                getItemType().finishUsing(stack,world,cast)
            }
        }
        else{
            if(entity is LivingEntity){
                getItemType().useOnEntity(stack, cast, entity, Hand.MAIN_HAND)
            }
            entity.interact(cast,Hand.MAIN_HAND)
        }
        cast.setStackInHand(Hand.MAIN_HAND, old_hand_item)
    }

    override fun testEntity(cast: PlayerEntity, world: ServerWorld, entity: Entity): Boolean {
        return entity is LivingEntity && (entity.getStackInHand(Hand.MAIN_HAND).item==getItemType()) && entity.getStackInHand(Hand.OFF_HAND).item==getItemType()
    }

    override fun testPos(cast: PlayerEntity, world: ServerWorld, position: Vec3d): Boolean {
        val entities=world.getEntitiesByType( EntityType.ITEM, Box.of(position, 0.7, 0.7, 0.7), Predicates.alwaysTrue())
        return entities.any { entity -> entity.stack.item==getItemType() }
    }

    override fun canDrop(cast: PlayerEntity, world: ServerWorld, position: Vec3d, power: Int): Int {
        return 10
    }

    override fun drop(cast: PlayerEntity, world: ServerWorld, position: Vec3d, power: Int): Iota {
        /*val stack=getItemType().defaultStack;
        stack.count=power;
        val entity=EntityType.ITEM.create(world);
        if(entity!=null){
            entity.stack=stack
            entity.setPosition(position)
            world.spawnEntity(entity)
            return EntityIota(entity)
        }
        return NullIota()*/
        val stack=getItemType().defaultStack
        stack.count=1
        val old_hand_item=cast.mainHandStack
        cast.setStackInHand(Hand.MAIN_HAND,stack)
        getItemType().useOnBlock(
            ItemUsageContext(
                cast,
                Hand.MAIN_HAND,
                BlockHitResult(position, Direction.UP, BlockPos(position), true)
            )
        )
        cast.setStackInHand(Hand.MAIN_HAND, old_hand_item)
        return NullIota()
    }

}