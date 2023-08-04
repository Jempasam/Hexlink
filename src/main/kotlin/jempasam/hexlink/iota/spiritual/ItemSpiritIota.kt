package jempasam.hexlink.iota.spiritual

import at.petrak.hexcasting.api.spell.iota.EntityIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.IotaType
import at.petrak.hexcasting.api.spell.iota.NullIota
import at.petrak.hexcasting.api.utils.italic
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import com.google.common.base.Predicates
import hexlink.iota.BlockTypeIota
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.UseAction
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.Registry

class ItemSpiritIota(val item: Item): Iota(TYPE, item), SpiritIota {

    fun getItemType(): Item = payload as Item

    override fun toleratesOther(that: Iota): Boolean = that is ItemSpiritIota && that.getItemType()==getItemType()

    override fun isTruthy(): Boolean = getItemType()!= Items.AIR

    override fun serialize(): NbtElement {
        return NbtString.of(Registry.ITEM.getId(getItemType()).toString());
    }

    companion object{
        val TYPE=object : IotaType<ItemSpiritIota>(){
            override fun color(): Int = 0xF1F400;

            override fun deserialize(tag: NbtElement, world: ServerWorld): ItemSpiritIota?{
                if(tag is NbtString){
                    var type=Registry.ITEM.getOrEmpty(Identifier(tag.asString())).orElseThrow(::IllegalArgumentException)
                    return ItemSpiritIota(type)
                }
                else throw IllegalArgumentException()
            }

            override fun display(tag: NbtElement): Text {
                if(tag is NbtString){
                    var type=Registry.ITEM.getOrEmpty(Identifier(tag.asString()));
                    if(!type.isEmpty()){
                        return type.get().getName().copy().append(Text.translatable("hexlink.spirit"))
                    }
                }
                return Text.of("Invalid Block Spirit");
            }
        }
    }

    override fun canInfuse(world: ServerWorld, entity: Entity, power: Int): Int {
        return 10
    }

    override fun infuse(world: ServerWorld, entity: Entity, power: Int) {
        val stack=getItemType().defaultStack
        stack.count=1
        if(entity is PlayerEntity){
            val old_hand_item=entity.mainHandStack
            entity.setStackInHand(Hand.MAIN_HAND,stack)

            val action=getItemType().getUseAction(stack)
            val success=getItemType().use(world, entity, Hand.MAIN_HAND)
            if(action!=UseAction.NONE && action!=UseAction.BLOCK && success.result.isAccepted){
                getItemType().onStoppedUsing(stack, world, entity, 0)
            }

            entity.setStackInHand(Hand.MAIN_HAND, old_hand_item)
        }
    }

    override fun testEntity(world: ServerWorld, entity: Entity): Boolean {
        return entity is LivingEntity && (entity.getStackInHand(Hand.MAIN_HAND).item==getItemType()) && entity.getStackInHand(Hand.OFF_HAND).item==getItemType()
    }

    override fun testPos(world: ServerWorld, position: Vec3d): Boolean {
        var entities=world.getEntitiesByType( EntityType.ITEM, Box.of(position, 0.7, 0.7, 0.7), Predicates.alwaysTrue())
        return entities.any { entity -> entity.stack.item==getItemType() }
    }

    override fun canDrop(world: ServerWorld, position: Vec3d, power: Int): Int {
        return 10
    }

    override fun drop(world: ServerWorld, position: Vec3d, power: Int): Iota {
        val stack=getItemType().defaultStack;
        stack.count=power;
        val entity=EntityType.ITEM.create(world);
        if(entity!=null){
            entity.stack=stack
            entity.setPosition(position)
            world.spawnEntity(entity)
            return EntityIota(entity)
        }
        return NullIota()
    }

}