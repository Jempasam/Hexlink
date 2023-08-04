package jempasam.hexlink.model.predicate

import at.petrak.hexcasting.api.item.IotaHolderItem
import net.minecraft.client.item.UnclampedModelPredicateProvider
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack

object FilledIotaHolderPredicate : UnclampedModelPredicateProvider {
    override fun unclampedCall(stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int): Float {
        val item=stack.item
        return if(item is IotaHolderItem && item.readIotaTag(stack)!=null) 1.0f else 0.0f;
    }
}