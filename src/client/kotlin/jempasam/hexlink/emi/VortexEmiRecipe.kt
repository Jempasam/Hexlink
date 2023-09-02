package jempasam.hexlink.emi

import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.WidgetHolder
import jempasam.hexlink.item.HexlinkItems
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.recipe.vortex.HexVortexHandler
import net.minecraft.text.Text
import net.minecraft.util.Identifier


class VortexEmiRecipe(private val id: Identifier, private val inputs: List<HexVortexHandler.Ingredient>, private val outputs: List<Spirit>) : EmiRecipe {
    override fun getCategory(): EmiRecipeCategory = HexlinkEMIPlugin.VORTEX

    override fun getId(): Identifier = id

    override fun getInputs(): List<EmiIngredient> {
        return inputs.map { ingredient(it) }
    }

    override fun getOutputs(): List<EmiStack> {
        return outputs.map { HexlinkEMIPlugin.stackOfSpirit(it) }
    }

    override fun getDisplayWidth(): Int = 144

    override fun getDisplayHeight(): Int = 60

    override fun supportsRecipeTree(): Boolean = false

    override fun addWidgets(widgets: WidgetHolder) {
        // Create bags
        val input_bag= mutableListOf<IngPair>()
        for(spirit in inputs){
            if(input_bag.isEmpty() || input_bag.last().ingredient!=spirit){
                input_bag.add(IngPair(spirit,1))
            }
            else input_bag.last().count++
        }

        val output_bag= mutableListOf<IngPair>()
        for(spirit in outputs){
            val ing= HexVortexHandler.Ingredient(spirit);
            if(output_bag.isEmpty() || output_bag.last().ingredient!=ing){
                output_bag.add(IngPair(ing,1))
            }
            else output_bag.last().count++
        }

        //for(spirit in outputs)output_bag.pushBack(spirit,1)

        drawSpiritsLine(widgets, input_bag, 1, false)

        val vortex=EmiStack.of(HexlinkItems.Vortex)
        widgets.addDrawable((displayWidth-slot_size)/2, slot_size+2, slot_size, slot_size, { stack,_,_,delta ->
            vortex.render(stack,0,0,delta)
        })

        drawSpiritsLine(widgets, output_bag, slot_size*2+3, true)
    }

    private fun drawSpiritsLine(widgets: WidgetHolder, spirits: List<IngPair>, height: Int, isContext: Boolean){
        val stack_width= slot_size+ slot_size/2
        val center_x=displayWidth/2 - spirits.size*(stack_width+1)/2
        for(i in 0..<spirits.size){
            val x=center_x+i*(stack_width+1)
            val slot=widgets.addSlot(ingredient(spirits[i].ingredient), x, height)
            if(isContext)slot.recipeContext(this)
            widgets.addText(Text.of(spirits[i].count.toString()), x+slot_size, height, 0xFFFFFF, true)
        }
    }

    private fun ingredient(spirits: HexVortexHandler.Ingredient): EmiIngredient{
        return EmiIngredient.of(spirits.map { HexlinkEMIPlugin.stackOfSpirit(it) }.toMutableList());
    }


    private data class IngPair(var ingredient: HexVortexHandler.Ingredient, var count: Int)

    companion object{
        const val slot_size=18
    }
}