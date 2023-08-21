package jempasam.hexlink.utils

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeType
import net.minecraft.recipe.ShapedRecipe
import net.minecraft.screen.ScreenHandler
import net.minecraft.world.World

object RecipeHelper {

    private val NONE_HANDLER=object:ScreenHandler(null,-1){
        override fun transferSlot(player: PlayerEntity?, index: Int): ItemStack = ItemStack.EMPTY
        override fun canUse(player: PlayerEntity?): Boolean = true
        override fun isValid(slot: Int): Boolean = true
    }

    fun <T: Recipe<CraftingInventory>>findAllSimpleMatches(world: World, type: RecipeType<T>, input: List<ItemStack>): List<T>{
        val recipe_manager=world.server?.recipeManager
        if(recipe_manager!=null){
            val recipes=recipe_manager.listAllOfType(type)
            val matched_recipes= mutableListOf<T>()
            for(recipe in recipes){
                // Check Shapeless Matches
                val ingredients=recipe.ingredients
                var stack_place=0

                for(ingre_place in input.indices){
                    val ingredient= ingredients[ingre_place]
                    if(ingredient.isEmpty)continue
                    if(ingredient.test(input[stack_place])){
                        stack_place++
                        if(stack_place==input.size){
                            matched_recipes.add(recipe)
                        }
                    }
                    else break
                }
            }
            return matched_recipes
        }
        else return listOf()
    }


    fun <T: Recipe<CraftingInventory>>craft(world: World, type: RecipeType<T>, input: List<ItemStack>): Pair<ItemStack,Int>?{
        // Sort matches, test with most items first
        var matches= findAllSimpleMatches(world, type, input).map{
            var ingredient_count=0
            for(ing in it.ingredients)if(!ing.isEmpty)ingredient_count++
            it to ingredient_count
        }
        matches=matches.sortedByDescending { it.second }

        // Test matches
        for(match in matches){
            val recipe=match.first
            val ingredients=recipe.ingredients
            val dimension = if(recipe is ShapedRecipe) recipe.width to recipe.height else recipe.ingredients.size to 1
            val craft=CraftingInventory(NONE_HANDLER, dimension.first, dimension.second)

            // Fill inventory
            var listi=0
            for(i in 0 until ingredients.size){
                val ingredient= ingredients[i]
                if(listi<input.size && !ingredient.isEmpty){
                    craft.setStack(i,input[listi])
                    listi++
                }
                else craft.setStack(i,ItemStack.EMPTY)
            }
            if(recipe.matches(craft,world))return recipe.craft(craft) to ingredients.size
        }
        return null
    }
}