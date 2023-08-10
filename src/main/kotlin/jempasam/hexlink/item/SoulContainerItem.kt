package jempasam.hexlink.item

import at.petrak.hexcasting.api.utils.asCompound
import at.petrak.hexcasting.api.utils.getOrCreateList
import jempasam.hexlink.item.functionnality.ExtractorItem
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.utils.NbtHelper
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.Entity
import net.minecraft.inventory.Inventory
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.collection.DefaultedList
import net.minecraft.world.World

class SoulContainerItem(settings: Settings, val max_box_count: Int, val max_soul_count: Int): Item(settings), ExtractorItem {

    fun souls(stack: ItemStack): NbtList? = stack.nbt?.getList("souls", NbtElement.COMPOUND_TYPE.toInt())

    fun soulsOrCreate(stack: ItemStack): NbtList = stack.orCreateNbt.getOrCreateList("souls", NbtElement.COMPOUND_TYPE)

    fun findEntryNbt(stack: ItemStack, spirit: Spirit): NbtCompound?{
        val souls=souls(stack)
        if(souls==null)return null
        val key=NbtHelper.writeSpirit(spirit)
        val removed= mutableListOf<NbtElement>()
        for(entry in souls){
            val spirit_nbt=entry.asCompound.get("spirit")
            if(spirit_nbt!=null){
                if(entry.asCompound.getInt("count")==0){
                    removed.add(entry)
                }
                else{
                    println(spirit_nbt.toString()+" and "+key.toString()+" = "+(spirit_nbt.equals(key)))
                    if(spirit_nbt.equals(key))return entry.asCompound
                }

            }
            else removed.add(entry)
        }
        removed.forEach { souls.remove(it) }
        return null
    }

    fun findEntryNbtOrCreate(stack: ItemStack, spirit: Spirit): NbtCompound{
        val entry=findEntryNbt(stack,spirit)
        if(entry==null){
            val souls=soulsOrCreate(stack)
            val new_entry=NbtCompound()
            new_entry.put("spirit",NbtHelper.writeSpirit(spirit))
            new_entry.putInt("count",0)
            souls.add(new_entry)
            return new_entry
        }
        else return entry
    }

    override fun canExtractFrom(stack: ItemStack, target: Entity): Boolean {
        val extractor=getExtractor(stack)
        return extractor!=null && extractor.canExtract(target)
    }

    override fun extractFrom(stack: ItemStack, target: Entity): ExtractorItem.ExtractionResult {
        val extractor=getExtractor(stack)
        if(extractor!=null){
            val extract_result=extractor.extract(target)
            if( findEntryNbt(stack,extract_result.spirit)!=null || ((souls(stack)?.size?:0)<max_box_count) ){
                val entry=findEntryNbtOrCreate(stack,extract_result.spirit)
                val new_value=Math.min(entry.getInt("count")+extract_result.count, max_soul_count)
                val offset=new_value-entry.getInt("count")
                if(offset==0)return ExtractorItem.ExtractionResult.FAIL
                entry.putInt("count",new_value)
                return ExtractorItem.ExtractionResult.SUCCESS
            }
        }
        return ExtractorItem.ExtractionResult.FAIL
    }

    fun consumeSpirit(stack: ItemStack, spirit: Spirit): Boolean{
        val entry=findEntryNbt(stack,spirit)
        if(entry==null)return false
        val count=entry.getInt("count")
        if(count<=0){
            souls(stack)?.remove(entry)
            return false
        }
        entry.putInt("count", count-1)
        if(count<=1)souls(stack)?.remove(entry)
        return true
    }

    fun canConsumeSpirit(stack: ItemStack, spirit: Spirit): Boolean{
        val entry=findEntryNbt(stack,spirit)
        if(entry==null)return false
        val count=entry.getInt("count")
        if(count<=0){
            souls(stack)?.remove(entry)
            return false
        }
        return true
    }

    override fun isItemBarVisible(stack: ItemStack): Boolean = true

    override fun getItemBarStep(stack: ItemStack): Int {
        val souls=souls(stack)
        return (13.0f*(souls?.size ?: 0)/max_box_count).toInt()
    }

    override fun getItemBarColor(stack: ItemStack): Int {
        return getExtractor(stack)?.getColor() ?: DyeColor.LIME.fireworkColor
    }

    override fun getName(stack: ItemStack): Text = getExtractorName(stack)

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        appendExtractorTooltip(stack, tooltip)
        for(entry in souls(stack) ?: NbtList()){
            val compound=entry.asCompound
            val spirit=NbtHelper.readSpirit(compound.getCompound("spirit"))
            if(spirit!=null){
                val count=compound.getInt("count")
                if(count>0){
                    tooltip.add(
                            spirit.getName().copy()
                                    .append(Text.of(" ("))
                                    .append(Text.of(count.toString()))
                                    .append(Text.of(")"))
                    )
                }
            }
        }
    }

    override fun appendStacks(group: ItemGroup?, stacks: DefaultedList<ItemStack>) {
        if (isIn(group)) appendStacks(this,stacks)
    }

     companion object{
         fun getSpiritConsumable(inventory: Inventory, spirit: Spirit): ItemStack?{
             for(i in 0 until inventory.size()){
                 val stack=inventory.getStack(i)
                 val item=stack.item
                 if(item is SoulContainerItem && item.canConsumeSpirit(stack, spirit))
                     return stack
             }
             return null
         }
     }
}