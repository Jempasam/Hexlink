package jempasam.hexlink.recipe

import jempasam.hexlink.HexlinkMod
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object HexlinkRecipes {
    private fun <T: Recipe<*>>create(id: String, serializer: RecipeSerializer<T>): RecipeSerializer<T>{
        Registry.register(Registry.RECIPE_SERIALIZER, Identifier(HexlinkMod.MODID, id), serializer)
        println(Identifier(HexlinkMod.MODID, id).toString())
        return serializer
    }

    fun registerRecipes(){
        create("mixed_pigment", MixedPigmentRecipe.SERIALIZER)
        create("shaped_extractor", ExtractorShapedRecipe.SERIALIZER)
    }
}