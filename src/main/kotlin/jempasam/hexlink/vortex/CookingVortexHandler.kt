package jempasam.hexlink.vortex

import jempasam.hexlink.spirit.ItemSpirit
import jempasam.hexlink.spirit.Spirit
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeManager
import net.minecraft.recipe.RecipeType
import net.minecraft.server.world.ServerWorld

class CookingVortexHandler<R: Recipe<Inventory>>(val catlzer: Spirit, val recipe_type: RecipeType<R>, val multiplier: Float) : CatalyzedVortexHandler{

    private val recipe_manager=RecipeManager.createCachedMatchGetter(recipe_type)

    override fun getCatalyzer(): Spirit = catlzer

    override fun findRecipe(ingredients: List<Spirit>, world: ServerWorld): HexVortexHandler.Recipe? {
        if(ingredients.size>=2){
            val first=ingredients[0]
            val ingredient=ingredients[1]
            if(first==catlzer && ingredient is ItemSpirit){
                val inventory=SimpleInventory()
                inventory.setStack(0, ingredient.item.defaultStack)
                val cooking_recipe=recipe_manager.getFirstMatch(inventory,world)
                if(cooking_recipe.isPresent){
                    return Recipe(inventory,cooking_recipe.get(),this,world)
                }
            }
        }
        return null
    }

    class Recipe(val inv: Inventory, val rec: net.minecraft.recipe.Recipe<Inventory>, val handler: CookingVortexHandler<*>, val world: ServerWorld): HexVortexHandler.Recipe{
        override fun test(ingredients: List<Spirit>): Boolean {
            if(ingredients.size==2 && ingredients.get(0)==handler.catlzer){
                val item=ingredients.get(1)
                if(item is ItemSpirit){
                    inv.setStack(0,item.item.defaultStack)
                    return rec.matches(inv,world)
                }
            }
            return false
        }

        override fun ingredientCount(): Int = 2

        override fun mix(ingredients: List<Spirit>): List<Spirit> {
            val item=(ingredients.get(1) as ItemSpirit).item.defaultStack
            inv.setStack(0,item)
            val stack=rec.craft(inv)
            val count=Math.min((stack.count*handler.multiplier).toInt(),1)
            //TODO Use count for spirit count
            return listOf(ItemSpirit(rec.craft(inv).item))
        }
    }
}