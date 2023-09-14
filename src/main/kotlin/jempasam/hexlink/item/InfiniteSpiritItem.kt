package jempasam.hexlink.item

import jempasam.hexlink.item.functionnality.ItemScrollable
import jempasam.hexlink.item.functionnality.ItemSpiritSource
import jempasam.hexlink.item.functionnality.ItemSpiritTarget
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.inout.SpiritSource
import jempasam.hexlink.spirit.inout.SpiritTarget
import jempasam.hexlink.utils.NbtAdapterList
import jempasam.hexlink.utils.NbtHelper
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.world.World

class InfiniteSpiritItem(settings: Settings): Item(settings), ItemSpiritSource, ItemSpiritTarget, ItemScrollable {

    fun getSpirits(stack: ItemStack): SpiritList
        = SpiritList(stack.nbt?.getList("spirits",NbtElement.COMPOUND_TYPE.toInt()) ?: NbtList())

    override fun getSpiritSource(stack: ItemStack): SpiritSource {
        return object:SpiritSource{
            override fun last(): Spirit? = getSpirits(stack).run { if(size>0) this[0] else null }
            override fun extract(count: Int, spirit: Spirit): SpiritSource.SpiritOutputFlux {
                if(getSpirits(stack).contains(spirit))return SpiritSource.SpiritOutputFlux({}, count)
                else return SpiritSource.NONE.FLUX
            }
        }
    }

    override fun getSpiritTarget(stack: ItemStack): SpiritTarget {
        return object:SpiritTarget{
            override fun fill(count: Int, spirit: Spirit): SpiritTarget.SpiritInputFlux {
                val spirits=getSpirits(stack)
                if(spirits.size>0)
                    return SpiritTarget.SpiritInputFlux({spirits.add(spirit)}, 1)
                else
                    return SpiritTarget.SpiritInputFlux({}, 0)
            }
        }
    }


    override fun getName(stack: ItemStack): Text{
        return getSpirits(stack).first?.getName()?.let{ Text.translatable(getTranslationKey(stack), it)}
                ?: Text.translatable(getTranslationKey(stack)+".none")
    }

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        for(spirit in getSpirits(stack)){
            tooltip.add(spirit.getName().copy().setStyle(Style.EMPTY.withColor(spirit.getColor()).withItalic(true)))
        }

    }




    class SpiritList(nbt: NbtList): NbtAdapterList<Spirit>(nbt){
        override fun from(e: NbtElement): Spirit? = if(e is NbtCompound) NbtHelper.readSpirit(e) else null
        override fun to(e: Spirit): NbtElement = NbtHelper.writeSpirit(e)
    }

    override fun roll(stack: ItemStack, player: ServerPlayerEntity, hand: Hand, delta: Double) {
        val spirits=getSpirits(stack)
        if(spirits.size>1){
            if(delta>0){
                val top=spirits[0]
                if(top!=null)spirits.add(top)
                spirits.remove(0)
            }
            else{
                val top=spirits[spirits.size-1]
                if(top!=null)spirits.add(0, top)
                spirits.remove(spirits.size-1)
            }
        }
    }
}