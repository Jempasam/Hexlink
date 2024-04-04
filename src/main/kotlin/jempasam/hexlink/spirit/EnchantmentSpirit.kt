package jempasam.hexlink.spirit

import jempasam.hexlink.utils.EnchantHelper
import jempasam.hexlink.utils.NbtHelper
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtElement
import net.minecraft.registry.Registries
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.math.Vec3d
import kotlin.math.min

class EnchantmentSpirit(val enchantment: Enchantment): Spirit {

    override fun getName(): Text = Text.translatable(enchantment.translationKey)

    override fun getColor(): Int = DyeColor.MAGENTA.fireworkColor

    override fun equals(other: Any?): Boolean = other is EnchantmentSpirit && enchantment==other.enchantment

    override fun hashCode(): Int = enchantment.hashCode()*39

    override fun manifestAt(caster: LivingEntity?, world: ServerWorld, position: Vec3d, count: Int): Spirit.Manifestation {
        return manifestStack(StackHelper.stack(caster as? PlayerEntity,world,position), count)
    }
    override fun manifestIn(caster: LivingEntity?, world: ServerWorld, entity: Entity, count: Int): Spirit.Manifestation {
        return manifestStack(StackHelper.stack(caster as? PlayerEntity,entity), count)
    }
    private fun manifestStack(worldStack: StackHelper.WorldStack?, count: Int): Spirit.Manifestation{
        if(worldStack==null)return Spirit.NONE_MANIFESTATION
        if(worldStack.stack.isEnchantable && enchantment.isAcceptableItem(worldStack.stack)){
            val level= min((count.countTrailingZeroBits()+1), enchantment.maxLevel)
            val cost=1 shl (level-1)
            val new_stack= EnchantHelper.enchant(worldStack.stack, enchantment, level) ?: return Spirit.NONE_MANIFESTATION
            return Spirit.Manifestation(1, cost){
                worldStack.replace(new_stack)
            }
        }
        return Spirit.NONE_MANIFESTATION
    }

    override fun lookIn(caster: LivingEntity?, world: ServerWorld, entity: Entity): Boolean {
        return lookStack(StackHelper.stack(caster as? PlayerEntity,entity))
    }
    override fun lookAt(caster: LivingEntity?, world: ServerWorld, position: Vec3d): Boolean {
        return lookStack(StackHelper.stack(caster as? PlayerEntity,world,position))
    }
    private fun lookStack(worldStack: StackHelper.WorldStack?): Boolean{
        return worldStack!=null && EnchantmentHelper.getLevel(enchantment,worldStack.stack)!=0
    }

    override fun serialize(): NbtElement {
        return NbtHelper.writeRegistry(Registries.ENCHANTMENT,enchantment)
    }


    override fun getType(): Spirit.SpiritType<*> = Type
    object Type: Spirit.SpiritType<EnchantmentSpirit>{
        override fun getName(): Text = Text.translatable("hexlink.spirit.enchantment")
        override fun deserialize(nbt: NbtElement): EnchantmentSpirit? {
            return NbtHelper.readRegistry(Registries.ENCHANTMENT,nbt)?.let { EnchantmentSpirit(it) }
        }
    }
}