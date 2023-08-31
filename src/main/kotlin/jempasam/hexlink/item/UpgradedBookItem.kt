package jempasam.hexlink.item

import at.petrak.hexcasting.api.spell.casting.CastingHarness
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.ListIota
import at.petrak.hexcasting.api.spell.iota.PatternIota
import at.petrak.hexcasting.common.items.ItemSpellbook
import at.petrak.hexcasting.xplat.IXplatAbstractions
import jempasam.hexlink.item.functionnality.ItemScrollable
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World


class UpgradedBookItem(settings: Settings): ItemSpellbook(settings), ItemScrollable{

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack>{
        val stack = user.getStackInHand(hand)
        if(world is ServerWorld){
            val iota= readIota(stack, world) ?: return TypedActionResult.fail(stack)

            val harness = IXplatAbstractions.INSTANCE.getHarness(user as ServerPlayerEntity, hand)
            val success = useIota(iota, harness)
            IXplatAbstractions.INSTANCE.setHarness(user, harness)
            return if(success) TypedActionResult.success(stack) else TypedActionResult.fail(stack)
        }
        return TypedActionResult.success(stack)
    }

    fun useIota(iota: Iota, harness: CastingHarness, doList: Boolean=true): Boolean{
        if(iota is PatternIota){
            val info = harness.executeIotas(listOf(iota), harness.ctx.caster.getWorld())
            if(!info.resolutionType.success)return false
        }
        else if(iota is ListIota && doList){
            for(element in iota.list){
                val success=useIota(element, harness, false)
                if(!success)return false
            }
        }
        else{
            harness.stack.add(iota)
        }
        return true
    }

    override fun roll(stack: ItemStack, player: ServerPlayerEntity, hand: Hand, delta: Double) {
        val newIdx = rotatePageIdx(stack, delta < 0.0)
        val len = highestPage(stack)
        val sealed = isSealed(stack)

        val component= if (hand == Hand.OFF_HAND && stack.hasCustomName()) {
            if (sealed) {
                Text.translatable("hexcasting.tooltip.spellbook.page_with_name.sealed",
                        Text.literal(newIdx.toString()).formatted(Formatting.WHITE),
                        Text.literal(len.toString()).formatted(Formatting.WHITE),
                        Text.literal("").formatted(stack.rarity.formatting, Formatting.ITALIC)
                                .append(stack.getName()),
                        Text.translatable("hexcasting.tooltip.spellbook.sealed").formatted(Formatting.GOLD))
            } else {
                Text.translatable("hexcasting.tooltip.spellbook.page_with_name",
                        Text.literal(newIdx.toString()).formatted(Formatting.WHITE),
                        Text.literal(len.toString()).formatted(Formatting.WHITE),
                        Text.literal("").formatted(stack.rarity.formatting, Formatting.ITALIC)
                                .append(stack.getName()))
            }
        } else {
            if (sealed) {
                Text.translatable("hexcasting.tooltip.spellbook.page.sealed",
                        Text.literal(newIdx.toString()).formatted(Formatting.WHITE),
                        Text.literal(len.toString()).formatted(Formatting.WHITE),
                        Text.translatable("hexcasting.tooltip.spellbook.sealed").formatted(Formatting.GOLD))
            } else {
                Text.translatable("hexcasting.tooltip.spellbook.page",
                        Text.literal(newIdx.toString()).formatted(Formatting.WHITE),
                        Text.literal(len.toString()).formatted(Formatting.WHITE))
            }
        }

        player.sendMessage(component.formatted(Formatting.GRAY), true)
    }
}