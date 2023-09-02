package jempasam.hexlink.item

import at.petrak.hexcasting.api.spell.casting.ControllerInfo
import at.petrak.hexcasting.api.spell.casting.ResolvedPatternType
import at.petrak.hexcasting.api.spell.iota.ListIota
import at.petrak.hexcasting.api.spell.iota.PatternIota
import at.petrak.hexcasting.api.spell.math.HexPattern
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import at.petrak.hexcasting.xplat.IXplatAbstractions
import jempasam.hexlink.item.functionnality.SpellCasterItem
import jempasam.hexlink.item.functionnality.SpellHolderItem
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class SpecialWandItem(settings: Settings): Item(settings), SpellCasterItem, SpellHolderItem {
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        return SpellCasterItem.activeSpellCastingGUI(world,user,hand)
    }
    override fun getSpellNbt(stack: ItemStack): NbtList? = stack.nbt?.getList("spell",NbtElement.COMPOUND_TYPE.toInt())

    override fun setSpellNbt(stack: ItemStack, list: NbtList) { stack.orCreateNbt.put("spell",list) }

    override fun onCast(stack: ItemStack, hand: Hand, caster: ServerPlayerEntity, pattern: HexPattern): ControllerInfo {
        if(hasSpell(stack)){
            val spell=getSpell(stack, caster.getWorld())
            val harness = IXplatAbstractions.INSTANCE.getHarness(caster,hand)
            harness.stack= mutableListOf(
                ListIota(harness.stack),
                PatternIota(pattern)
            )
            val info = harness.executeIotas(spell, caster.getWorld())
            if(info.resolutionType==ResolvedPatternType.EVALUATED){
                val pharness= IXplatAbstractions.INSTANCE.getHarness(caster,hand)
                pharness.stack=if(harness.stack.size>0){
                    val added=harness.stack[0]
                    if(added is ListIota) added.list.toMutableList()
                    else mutableListOf(added)
                }
                else mutableListOf()
                IXplatAbstractions.INSTANCE.setHarness(caster,pharness)
            }
            val pstack=IXplatAbstractions.INSTANCE.getHarness(caster, hand).stack.map { HexIotaTypes.serialize(it) }
            return ControllerInfo(
                    pstack.isEmpty(),
                    info.resolutionType,
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