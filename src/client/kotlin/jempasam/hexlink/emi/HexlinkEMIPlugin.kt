package jempasam.hexlink.emi


import dev.emi.emi.api.EmiPlugin
import dev.emi.emi.api.EmiRegistry
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.stack.Comparison
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.HexlinkRegistry
import jempasam.hexlink.item.HexlinkItems
import jempasam.hexlink.spirit.BlockSpirit
import jempasam.hexlink.spirit.ItemSpirit
import jempasam.hexlink.spirit.SpecialSpirit
import jempasam.hexlink.spirit.Spirit
import net.minecraft.item.Items
import net.minecraft.util.Identifier

class HexlinkEMIPlugin : EmiPlugin{

    companion object{

        val VORTEX=EmiRecipeCategory(Identifier(HexlinkMod.MODID,"vortex"), EmiStack.of(HexlinkItems.Vortex.defaultStack))

        fun stackOfSpirit(spirit: Spirit): EmiStack{
            if(spirit is ItemSpirit)
                return EmiStack.of(spirit.item.defaultStack)
            if(spirit is BlockSpirit){
                if(spirit.block.asItem()!= Items.AIR)
                    return EmiStack.of(spirit.block.asItem().defaultStack)
            }
            return HexlinkItems.Spirit.let{
                val stack=it.defaultStack
                it.setSpirit(stack, spirit)
                EmiStack.of(stack)
            }
        }

        fun ingOfSpirit(spirit: Spirit): EmiIngredient = stackOfSpirit(spirit)

    }

    override fun register(registry: EmiRegistry) {
        // Categories
        registry.addWorkstation(VORTEX,EmiStack.of(HexlinkItems.Vortex))
        registry.addCategory(VORTEX)

        // Stacks
        val spirits_stacks= HexlinkRegistry.HEXVORTEX_HANDLER.asSequence()
                .flatMap { it.getRecipesExamples(registry.recipeManager) }
                .flatMap { it.second }
                .filter {it !is ItemSpirit && it !is BlockSpirit }
                .toMutableSet()
                .also { it.removeAll(HexlinkRegistry.SPECIAL_SPIRIT .asSequence().map { SpecialSpirit(it) }) }
                .asSequence()


        // Comparaison
        registry.setDefaultComparison(
                EmiStack.of(HexlinkItems.Spirit),
                Comparison.compareData { HexlinkItems.Spirit.getSpirit(it.itemStack) }
        )


        for(spirit in spirits_stacks){
            val stack=HexlinkItems.Spirit.defaultStack
            HexlinkItems.Spirit.setSpirit(stack, spirit)
            registry.addEmiStack(EmiStack.of(stack))
        }

        // Recipes
        for(handler in HexlinkRegistry.HEXVORTEX_HANDLER.entrySet){
            var i=0
            for(recipe in handler.value.getRecipesExamples(registry.recipeManager)){
                val id=Identifier(handler.key.value.namespace, handler.key.value.path+i)
                registry.addRecipe(VortexEmiRecipe(id, recipe.first, recipe.second))
                i++
            }
        }
    }

}