package jempasam.hexlink.item

import at.petrak.hexcasting.api.casting.eval.ExecutionClientView
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.xplat.IXplatAbstractions
import jempasam.hexlink.item.functionnality.SpellCasterItem
import jempasam.hexlink.item.functionnality.SpellHolderItem
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.DyeableItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class SpecialWandItem(settings: Settings): Item(settings), SpellCasterItem, SpellHolderItem, DyeableItem {
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        return SpellCasterItem.activeSpellCastingGUI(world,user,hand)
    }
    override fun getSpellNbt(stack: ItemStack): NbtList? = stack.nbt?.getList("spell",NbtElement.COMPOUND_TYPE.toInt())

    override fun setSpellNbt(stack: ItemStack, list: NbtList) { stack.orCreateNbt.put("spell",list) }

    override fun onCast(stack: ItemStack, hand: Hand, caster: ServerPlayerEntity, pattern: HexPattern): ExecutionClientView {
        if(hasSpell(stack)){
            // Get machine and image
            val machine = IXplatAbstractions.INSTANCE.getStaffcastVM(caster,hand)
            /*if(harness.parenCount>0){
                harness.parenCount=0
                harness.stack.addAll(harness.parenthesized)
            }*/

            // Execute spell
            val spell=getSpell(stack, caster.serverWorld)
            machine.image=machine.image.copy(
                stack= mutableListOf(
                    ListIota(machine.image.stack),
                    PatternIota(pattern)
                )
            )
            val info = machine.queueExecuteAndWrapIotas(spell, caster.serverWorld)

            var resolution=info.resolutionType
            if(resolution==ResolvedPatternType.EVALUATED){
                // Fetch Stack
                val new_stack=machine.image.stack
                    .lastOrNull() ?.let { it as? ListIota } ?.list ?.toMutableList() ?: mutableListOf()

                // Fetch Result Code
                val iota_result=machine.image.stack
                    .dropLast(1) .lastOrNull() ?.let { it as? DoubleIota }?.double?.toInt() ?: -1

                resolution=when{
                    iota_result==1 -> ResolvedPatternType.UNRESOLVED
                    iota_result==2 -> ResolvedPatternType.ESCAPED
                    iota_result==3 -> ResolvedPatternType.INVALID
                    iota_result==4 -> ResolvedPatternType.ERRORED
                    else -> ResolvedPatternType.EVALUATED
                }
            }
            //pharness.parenthesized= mutableListOf()
            //pharness.parenCount= 0
            //pharness.escapeNext= false
            IXplatAbstractions.INSTANCE.setStaffcastImage(caster,machine.image)

            val pstack=machine.image.stack.map { IotaType.serialize(it) }
            return ExecutionClientView(
                    pstack.isEmpty(),
                    resolution,
                    listOf(),
                    info.ravenmind
            )
        }
        return ExecutionClientView(
                true,
                ResolvedPatternType.ERRORED,
                listOf(),
                null
        )
    }

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        appendSpellTooltip(stack,world,tooltip,context)
    }

}