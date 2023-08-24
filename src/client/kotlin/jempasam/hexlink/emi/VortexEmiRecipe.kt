package jempasam.hexlink.emi

import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.WidgetHolder
import jempasam.hexlink.item.HexlinkItems
import jempasam.hexlink.spirit.Spirit
import net.minecraft.util.Identifier


class VortexEmiRecipe(private val id: Identifier, private val inputs: List<Spirit>, private val outputs: List<Spirit>) : EmiRecipe {
    override fun getCategory(): EmiRecipeCategory = HexlinkEMIPlugin.VORTEX

    override fun getId(): Identifier = id

    override fun getInputs(): List<EmiIngredient> {
        return inputs.map { HexlinkEMIPlugin.ingOfSpirit(it) }
    }

    override fun getOutputs(): List<EmiStack> {
        return inputs.map { HexlinkEMIPlugin.stackOfSpirit(it) }
    }

    override fun getDisplayWidth(): Int = 144

    override fun getDisplayHeight(): Int = 53

    override fun supportsRecipeTree(): Boolean = false

    override fun addWidgets(widgets: WidgetHolder) {
        val input_x_start=74-inputs.size*17/2
        val output_x_start=74-outputs.size*17/2
        for(i in inputs.indices){
            widgets.addSlot(HexlinkEMIPlugin.stackOfSpirit(inputs[i]), input_x_start+i*17, 1)
        }
        widgets.addSlot(EmiStack.of(HexlinkItems.Vortex), 64, 19).catalyst(true)
        for(i in outputs.indices){
            widgets.addSlot(HexlinkEMIPlugin.stackOfSpirit(outputs[i]), output_x_start+i*17, 36).recipeContext(this)
        }
    }
}