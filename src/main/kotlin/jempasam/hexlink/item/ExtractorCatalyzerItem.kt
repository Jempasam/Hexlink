package jempasam.hexlink.item

import jempasam.hexlink.item.functionnality.ExtractorItem
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.collection.DefaultedList
import net.minecraft.world.World

class ExtractorCatalyzerItem(settings: Settings) : Item(settings), ExtractorItem {
    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        appendExtractorTooltip(stack, tooltip)
    }

    override fun appendStacks(group: ItemGroup, stacks: DefaultedList<ItemStack>){
        if(isIn(group))appendStacks(this,stacks)
    }

    override fun getName(stack: ItemStack): Text = getExtractorName(stack)


}