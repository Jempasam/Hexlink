package jempasam.hexlink.item

import at.petrak.hexcasting.api.spell.casting.CastingHarness
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.ListIota
import at.petrak.hexcasting.api.spell.iota.PatternIota
import at.petrak.hexcasting.common.items.ItemSpellbook
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World


class UpgradedBookItem(settings: Settings): ItemSpellbook(settings){

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
}