package jempasam.hexlink.item

import at.petrak.hexcasting.api.utils.asCompound
import at.petrak.hexcasting.api.utils.getOrCreateList
import at.petrak.hexcasting.api.utils.putCompound
import jempasam.hexlink.item.functionnality.ItemSpiritSource
import jempasam.hexlink.item.functionnality.ItemSpiritTarget
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.inout.SpiritSource
import jempasam.hexlink.spirit.inout.SpiritTarget
import jempasam.hexlink.utils.NbtHelper
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.world.World
import kotlin.math.min

class SoulContainerItem(settings: Settings, val max_box_count: Int, val max_soul_count: Int): Item(settings), ItemSpiritSource, ItemSpiritTarget {

    fun souls(stack: ItemStack): Souls?
        = stack.nbt?.getList("souls", NbtElement.COMPOUND_TYPE.toInt())?.let { Souls(it) }

    fun soulsOrCreate(stack: ItemStack): Souls
        = stack.orCreateNbt.getOrCreateList("souls", NbtElement.COMPOUND_TYPE).let { Souls(it) }

    class Souls(val nbt: NbtList): Sequence<SoulStack>{

        fun remove(stack: SoulStack) = nbt.remove(stack.nbt)
        fun remove(index: Int) = nbt.removeAt(index)

        val size: Int get()=nbt.size

        fun get(index: Int): SoulStack = SoulStack(nbt.getCompound(index))

        fun get(tofind: Spirit): SoulStack?{
            val key=NbtHelper.writeSpirit(tofind)
            val removed= mutableListOf<SoulStack>()
            for(entry in this){
                val spirit_nbt=entry.spirit_nbt
                if(!spirit_nbt.isEmpty){
                    if(entry.count==0) removed.add(entry)
                    else if(entry.spirit_nbt==key) return entry
                }
                else removed.add(entry)
            }
            removed.forEach{ remove(it) }
            return null
        }

        fun getOrCreate(spirit: Spirit): SoulStack{
            val entry=get(spirit)
            if(entry==null){
                val newEntry=NbtCompound()
                newEntry.put("spirit",NbtHelper.writeSpirit(spirit))
                newEntry.putInt("count",0)
                nbt.add(newEntry)
                return SoulStack(newEntry)
            }
            else return entry
        }

        fun add(spirit: Spirit, count: Int){
            val compound=NbtCompound()
            compound.putInt("count", count)
            compound.putCompound("spirit", NbtHelper.writeSpirit(spirit))
            nbt.add(compound)
        }

        override fun iterator(): Iterator<SoulStack> {
            return nbt.asSequence().map{ SoulStack(it.asCompound) }.iterator()
        }

    }

    class SoulStack(val nbt: NbtCompound){
        var count: Int
            get()= nbt.getInt("count")
            set(value)= nbt.putInt("count", value)

        var spirit: Spirit?
            get()= NbtHelper.readSpirit(nbt.getCompound("spirit"))
            set(value){
                if(value==null)nbt.remove("spirit")
                else nbt.putCompound("spirit", NbtHelper.writeSpirit(value))
            }

        val spirit_nbt: NbtCompound get() =  nbt.getCompound("spirit")
    }

    fun last(stack: ItemStack): Spirit?
        = souls(stack)?.let { if(it.size>0) it.get(it.size-1) else null }?.spirit

    override fun isItemBarVisible(stack: ItemStack): Boolean
        = souls(stack)?.size?.let { it!=max_box_count && it!=0 } ?: false

    override fun getItemBarStep(stack: ItemStack): Int {
        val souls=souls(stack)
        return (13.0f*(souls?.size ?: 0)/max_box_count).toInt()
    }

    override fun getItemBarColor(stack: ItemStack): Int {
        return last(stack)?.getColor() ?: DyeColor.MAGENTA.fireworkColor
    }

    override fun getName(stack: ItemStack): Text
        = last(stack) ?.getName() ?.let{Text.translatable(getTranslationKey(stack), it)}
            ?: Text.translatable(getTranslationKey(stack)+".none")

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        val souls=souls(stack) ?: return
        for(i in 0..<souls.size){
            val spiritstack=souls.get(i)
            val spirit=spiritstack.spirit ?: continue
            if(spiritstack.count>0){
                tooltip.add(
                        spirit.getName().copy()
                                .append(Text.of(" ("))
                                .append(Text.of(spiritstack.count.toString()))
                                .append(Text.of(")"))
                )
            }
        }
    }

    override fun getSpiritSource(stack: ItemStack): SpiritSource {
        return object: SpiritSource{
            override fun extract(count: Int, spirit: Spirit): SpiritSource.SpiritOutputFlux {
                print("Try to find "+spirit.getName()+" in "+souls(stack)?.nbt+" and find "+souls(stack)?.get(spirit)?.nbt)
                val souls=souls(stack) ?: return SpiritSource.NONE.FLUX
                val entry=souls.get(spirit) ?: return SpiritSource.NONE.FLUX
                print("Find:"+entry.nbt)
                val final_count=min(entry.count, count)
                return SpiritSource.SpiritOutputFlux(
                        {
                            val newcount=entry.count-it
                            if(newcount<=0)souls.remove(entry)
                            else entry.count=newcount
                        },
                        final_count
                )
            }

            override fun last(): Spirit? = last(stack)
        }
    }

    override fun getSpiritTarget(stack: ItemStack): SpiritTarget {
        return object: SpiritTarget{
            override fun fill(count: Int, spirit: Spirit): SpiritTarget.SpiritInputFlux {
                val souls=soulsOrCreate(stack)
                print("FillFind: "+souls.get(spirit)?.nbt+" size: "+souls.size+" max: "+max_box_count)
                if( souls.get(spirit)!=null || souls.size<max_box_count ){
                    val opt_entry=souls.get(spirit)
                    val ecount=opt_entry?.count ?: 0

                    println("from: "+ecount+" add "+count+" with max "+max_soul_count)
                    val new_value= min(ecount+count, max_soul_count)
                    val offset=new_value-ecount
                    println("offset: "+offset)
                    if(offset<=0)return SpiritTarget.NONE.FLUX

                    return SpiritTarget.SpiritInputFlux(
                            {
                                val entry=souls.getOrCreate(spirit)
                                println(entry)
                                entry.count+=min(it,offset)
                                println(entry)
                            },
                            offset
                    )
                }
                return SpiritTarget.NONE.FLUX
            }
        }
    }
}