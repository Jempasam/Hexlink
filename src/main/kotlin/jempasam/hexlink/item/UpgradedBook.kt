package jempasam.hexlink.item

import at.petrak.hexcasting.api.item.HexHolderItem
import at.petrak.hexcasting.common.items.ItemSpellbook
import at.petrak.hexcasting.api.item.IotaHolderItem
import at.petrak.hexcasting.api.item.MediaHolderItem
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.NullIota
import at.petrak.hexcasting.api.spell.iota.PatternIota
import at.petrak.hexcasting.api.spell.iota.ListIota
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.casting.CastingHarness
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import at.petrak.hexcasting.xplat.IXplatAbstractions
import org.jetbrains.annotations.Nullable

import java.util.stream.Stream
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.math.MathHelper
import net.minecraft.util.TypedActionResult
import net.minecraft.util.Hand
import net.minecraft.world.World
import net.minecraft.server.world.ServerWorld
import net.minecraft.server.network.ServerPlayerEntity


class UpgradedBook(settings: Item.Settings): ItemSpellbook(settings){

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack>{
        val stack = user.getStackInHand(hand);
        if(world is ServerWorld){
            val iota=readIota(stack, world);

            if(iota==null)return TypedActionResult.fail(stack);

            val harness = IXplatAbstractions.INSTANCE.getHarness(user as ServerPlayerEntity, hand)
            val success = useIota(iota, harness)
            IXplatAbstractions.INSTANCE.setHarness(user, harness)
            return if(success) TypedActionResult.success(stack) else TypedActionResult.fail(stack)
        }
        return TypedActionResult.success(stack)
    }

    fun useIota(iota: Iota, harness: CastingHarness, doList: Boolean=true): Boolean{
        if(iota is PatternIota){
            var info = harness.executeIotas(listOf(iota), harness.ctx.caster.getWorld())
            if(!info.resolutionType.success)return false
        }
        else if(iota is ListIota && doList){
            for(element in iota.getList()){
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