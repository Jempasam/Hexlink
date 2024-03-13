package jempasam.hexlink.item

import jempasam.hexlink.item.functionnality.ItemSpiritSource
import jempasam.hexlink.item.functionnality.ItemSpiritTarget
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.inout.SpiritSource
import jempasam.hexlink.spirit.inout.SpiritTarget
import jempasam.hexlink.utils.NbtHelper
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.world.World
import kotlin.math.min

class SingleSpiritContainerItem(settings: Settings): Item(settings), ItemSpiritSource, ItemSpiritTarget {
    fun setSpirit(stack: ItemStack, spirit: Spirit?){
        if(spirit==null)stack.nbt?.remove("spirit")
        else stack.orCreateNbt.put("spirit",NbtHelper.writeSpirit(spirit))
    }

    fun getSpirit(stack: ItemStack): Spirit?{
        return stack.nbt?.getCompound("spirit")?.let { NbtHelper.readSpirit(it) }
    }

    override fun getSpiritSource(stack: ItemStack): SpiritSource {
        return object:SpiritSource{
            override fun last(): Spirit? = getSpirit(stack)
            override fun extract(count: Int, spirit: Spirit): SpiritSource.SpiritOutputFlux {
                return SpiritSource.SpiritOutputFlux(min(count,stack.count)) { stack.count -= it }
            }
        }
    }

    override fun getSpiritTarget(stack: ItemStack): SpiritTarget {
        return object:SpiritTarget{
            override fun fill(count: Int, spirit: Spirit): SpiritTarget.SpiritInputFlux {
                if(getSpirit(stack)==null)return SpiritTarget.SpiritInputFlux({setSpirit(stack,spirit)},1)
                else return SpiritTarget.NONE.FLUX
            }
        }
    }

    override fun getName(stack: ItemStack): Text
            = getSpirit(stack) ?.getName() ?.let{ Text.translatable(getTranslationKey(stack), it)}
            ?: Text.translatable(getTranslationKey(stack)+".none")

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        val spirit=getSpirit(stack) ?: return
        tooltip.add(spirit.getName().copy().setStyle(Style.EMPTY.withColor(spirit.getColor()).withItalic(true)))
    }
}