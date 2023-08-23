package jempasam.hexlink.model.predicate

import jempasam.hexlink.item.functionnality.ItemSpiritSource
import net.minecraft.client.item.UnclampedModelPredicateProvider
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack

object FilledSpiritSourcePredicate : UnclampedModelPredicateProvider {
    override fun unclampedCall(stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int): Float {
        val item=stack.item
        return if(item is ItemSpiritSource && item.getSpiritSource(stack).last()!=null) 1.0f else 0.0f
    }
}