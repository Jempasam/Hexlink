package jempasam.hexlink.item

import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.utils.getCompound
import at.petrak.hexcasting.api.utils.getList
import at.petrak.hexcasting.api.utils.getOrCreateList
import at.petrak.hexcasting.api.utils.remove
import at.petrak.hexcasting.common.items.storage.ItemSpellbook
import at.petrak.hexcasting.xplat.IXplatAbstractions
import jempasam.hexlink.item.functionnality.ItemScrollable
import jempasam.hexlink.utils.IntListNbtAdapter
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.DyeableItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtList
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World


class UpgradedBookItem(settings: Settings): ItemSpellbook(settings), ItemScrollable, DyeableItem{

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack>{
        val stack = user.getStackInHand(hand)
        if(world is ServerWorld){
            val iota= readIota(stack, world) ?: return TypedActionResult.fail(stack)
            val machine = IXplatAbstractions.INSTANCE.getStaffcastVM(user as ServerPlayerEntity, hand)
            val (image,success) = useIota(iota, machine, world)
            IXplatAbstractions.INSTANCE.setStaffcastImage(user,image)
            return if(success) TypedActionResult.success(stack) else TypedActionResult.fail(stack)
        }
        return TypedActionResult.success(stack)
    }

    fun useIota(iota: Iota, machine: CastingVM, world: ServerWorld, doList: Boolean=true): Pair<CastingImage, Boolean>{
        if(iota is PatternIota){
            val info = machine.queueExecuteAndWrapIota(iota, world)
            if(!info.resolutionType.success)return machine.image to false
        }
        else if(iota is ListIota && doList){
            val info = machine.queueExecuteAndWrapIotas(iota.list.toList(), world)
            if(!info.resolutionType.success)return machine.image to false
        }
        else{
            val new_stack= machine.image.stack.toMutableList()
            new_stack.add(iota)
            return machine.image.copy(stack=new_stack) to true
        }
        return machine.image to true
    }

    fun colors(stack: ItemStack): IntListNbtAdapter
            = IntListNbtAdapter(stack.getList("colors",3) ?: NbtList());

    fun colorsOrCreate(stack: ItemStack): IntListNbtAdapter
            = IntListNbtAdapter(stack.getOrCreateList("colors",3));



    override fun writeDatum(stack: ItemStack, datum: Iota?) {
        super.writeDatum(stack, datum)
        val pages=stack.getCompound(TAG_PAGES)
        if(pages==null)stack.remove("colors")
        else{
            val colors=colors(stack)
            while(colors.size<pages.size) colors.add(DyeColor.YELLOW.fireworkColor)
            while(colors.size>pages.size) colors.remove(colors.size)
        }
    }

    override fun hasColor(stack: ItemStack): Boolean {
        val idx = getPage(stack, 1)
        val colors=colors(stack);
        return idx<=colors.size && colors[idx-1]!=-1
    }

    override fun getColor(stack: ItemStack): Int {
        val idx = getPage(stack, 1)
        val colors=colors(stack)
        return when{
            idx<=colors.size -> colors[idx-1] ?: -1
            else -> -1
        }
    }

    override fun removeColor(stack: ItemStack) {
        val idx = getPage(stack, 1)
        val colors=colors(stack)
        if(idx<=colors.size)colors[idx-1]=-1
        while(colors[colors.size-1]==-1)colors.remove(colors.size-1)
    }

    override fun setColor(stack: ItemStack, color: Int) {
        val idx = getPage(stack, 1)
        val colors=colorsOrCreate(stack)
        while(colors.size<idx) colors.add(-1)
        colors[idx-1]=color
    }

    fun getIotaColor(stack: ItemStack?): Int {
        return super<ItemSpellbook>.getColor(stack)
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