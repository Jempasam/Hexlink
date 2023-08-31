package jempasam.hexlink.item.functionnality

import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand

interface ItemScrollable {
    fun roll(stack: ItemStack, player: ServerPlayerEntity, hand: Hand, delta: Double)
}