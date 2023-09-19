package jempasam.hexlink.item

import at.petrak.hexcasting.api.spell.casting.ControllerInfo
import at.petrak.hexcasting.api.spell.casting.ResolvedPatternType
import at.petrak.hexcasting.api.spell.iota.DoubleIota
import at.petrak.hexcasting.api.spell.iota.ListIota
import at.petrak.hexcasting.api.spell.iota.PatternIota
import at.petrak.hexcasting.api.spell.math.HexPattern
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
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

    override fun onCast(stack: ItemStack, hand: Hand, caster: ServerPlayerEntity, pattern: HexPattern): ControllerInfo {
        if(hasSpell(stack)){
            // Close previous parenthese
            val harness = IXplatAbstractions.INSTANCE.getHarness(caster,hand)
            /*if(harness.parenCount>0){
                harness.parenCount=0
                harness.stack.addAll(harness.parenthesized)
            }*/

            // Execute spell
            val spell=getSpell(stack, caster.getWorld())
            harness.stack= mutableListOf(
                ListIota(harness.stack),
                PatternIota(pattern)
            )
            val info = harness.executeIotas(spell, caster.getWorld())

            var resolution=info.resolutionType
            val pharness= IXplatAbstractions.INSTANCE.getHarness(caster,hand)
            if(resolution==ResolvedPatternType.EVALUATED){
                // Fetch Stack
                val iota_stack=harness.stack
                    .lastOrNull() ?.let { it as? ListIota } ?.list ?.toMutableList() ?: mutableListOf()

                pharness.stack=iota_stack

                // Fetch Result Code
                val iota_result=harness.stack
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
            IXplatAbstractions.INSTANCE.setHarness(caster,pharness)

            val pstack=pharness.stack.map { HexIotaTypes.serialize(it) }
            return ControllerInfo(
                    pstack.isEmpty(),
                    resolution,
                    pstack,
                    listOf(),
                    info.ravenmind,
                    0
            )
        }
        return ControllerInfo(
                true,
                ResolvedPatternType.ERRORED,
                listOf(),
                listOf(),
                null,
                0
        )
    }

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        appendSpellTooltip(stack,world,tooltip,context)
    }

}