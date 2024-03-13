package jempasam.hexlink.item.functionnality

import at.petrak.hexcasting.api.casting.eval.ExecutionClientView
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.lib.HexSounds
import at.petrak.hexcasting.common.msgs.MsgOpenSpellGuiS2C
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.stat.Stats
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

interface SpellCasterItem {
    fun onCast(stack: ItemStack, hand: Hand, caster: ServerPlayerEntity, pattern: HexPattern): ExecutionClientView

    companion object{
        /**
         * Dont forget to add the item in "hexcasting:items/staves" tag
         */
        fun activeSpellCastingGUI(world: World, player: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
            if (player.isSneaking) {
                if (world.isClient())
                    player.playSound(HexSounds.CAST_FAILURE, 1f, 1f)
                else if (player is ServerPlayerEntity)
                    IXplatAbstractions.INSTANCE.clearCastingData(player)
            }
            if (!world.isClient() && player is ServerPlayerEntity) {
                val machine = IXplatAbstractions.INSTANCE.getStaffcastVM(player, hand)
                val patterns = IXplatAbstractions.INSTANCE.getPatternsSavedInUi(player)
                val (first,second) = machine.generateDescs()
                IXplatAbstractions.INSTANCE.sendPacketToPlayer(player,
                        MsgOpenSpellGuiS2C(hand, patterns, first, second, machine.image.parenCount)
                )
            }
            player.incrementStat(Stats.USED.getOrCreateStat(player.getStackInHand(hand).item))
            return TypedActionResult.success(player.getStackInHand(hand))
        }
    }
}