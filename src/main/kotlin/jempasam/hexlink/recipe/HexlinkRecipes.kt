package jempasam.hexlink.recipe

import jempasam.hexlink.HexlinkMod
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.util.Identifier
import net.minecraft.registry.Registry

object HexlinkRecipes {
    private fun <T: Recipe<*>>create(id: String, serializer: RecipeSerializer<T>): RecipeSerializer<T>{
        Registry.register(Registries.RECIPE_SERIALIZER, Identifier(HexlinkMod.MODID, id), serializer)
        return serializer
    }

    fun registerRecipes(){
        create("mixed_pigment", MixedPigmentRecipe.SERIALIZER)
        create("shaped_extractor", ExtractorShapedRecipe.SERIALIZER)
    }
}