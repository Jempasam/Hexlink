package jempasam.hexlink.item.functionnality

import at.petrak.hexcasting.api.spell.casting.ControllerInfo
import at.petrak.hexcasting.api.spell.math.HexPattern
import at.petrak.hexcasting.common.lib.HexSounds
import at.petrak.hexcasting.common.network.MsgOpenSpellGuiAck
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.stat.Stats
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

interface SpellCasterItem {
    fun onCast(stack: ItemStack, hand: Hand, caster: ServerPlayerEntity, pattern: HexPattern): ControllerInfo

    companion object{
        /**
         * Dont forget to add the item in "hexcasting:items/staves" tag
         */
        fun activeSpellCastingGUI(world: World, player: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
            if (player.isSneaking) {
                if (world.isClient())
                    player.playSound(HexSounds.FAIL_PATTERN, 1f, 1f)
                else if (player is ServerPlayerEntity)
                    IXplatAbstractions.INSTANCE.clearCastingData(player)
            }
            if (!world.isClient() && player is ServerPlayerEntity) {
                val harness = IXplatAbstractions.INSTANCE.getHarness(player, hand)
                val patterns = IXplatAbstractions.INSTANCE.getPatterns(player)
                val (first, second, third) = harness.generateDescs()
                IXplatAbstractions.INSTANCE.sendPacketToPlayer(player,
                        MsgOpenSpellGuiAck(hand, patterns, first, second, third,
                                harness.parenCount))
            }
            player.incrementStat(Stats.USED.getOrCreateStat(player.getStackInHand(hand).item))
            return TypedActionResult.success(player.getStackInHand(hand))
        }
    }
}