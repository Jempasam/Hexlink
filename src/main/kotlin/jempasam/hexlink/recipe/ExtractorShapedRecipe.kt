package jempasam.hexlink.recipe

import com.google.gson.JsonObject
import jempasam.hexlink.item.functionnality.ExtractorItem
import jempasam.hexlink.spirit.extractor.SpiritExtractor
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.CraftingRecipe
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.ShapedRecipe
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList
import net.minecraft.world.World

class ExtractorShapedRecipe(val recipe: ShapedRecipe) : CraftingRecipe {

    override fun craft(craftingInventory: CraftingInventory): ItemStack {
        val result=recipe.craft(craftingInventory)
        val resultItem=result.item
        if(resultItem is ExtractorItem){
            var extractor: SpiritExtractor<*>?=null
            for(i in 0 until craftingInventory.size()){
                val stack=craftingInventory.getStack(i)
                val item=stack.item
                if(item is ExtractorItem){
                    val ext=item.getExtractor(stack)
                    if(ext!=null && extractor==null)extractor=ext
                }
            }
            if(extractor!=null)resultItem.setExtractor(result, extractor)
        }
        return result
    }

    override fun matches(inventory: CraftingInventory, world: World): Boolean = recipe.matches(inventory,world)

    override fun getId(): Identifier = recipe.id

    override fun fits(width: Int, height: Int): Boolean = recipe.fits(width,height)

    override fun getOutput(): ItemStack = recipe.output

    override fun getIngredients(): DefaultedList<Ingredient> = recipe.ingredients

    override fun getGroup(): String = recipe.group

    override fun getSerializer(): RecipeSerializer<*> = SERIALIZER


    object SERIALIZER : RecipeSerializer<ExtractorShapedRecipe> {
        val SHAPELESS_SERIALIZER= ShapedRecipe.Serializer()
        override fun read(identifier: Identifier, jsonObject: JsonObject): ExtractorShapedRecipe
                = ExtractorShapedRecipe(SHAPELESS_SERIALIZER.read(identifier,jsonObject))

        override fun read(identifier: Identifier, packetByteBuf: PacketByteBuf): ExtractorShapedRecipe
                = ExtractorShapedRecipe(SHAPELESS_SERIALIZER.read(identifier,packetByteBuf))

        override fun write(packetByteBuf: PacketByteBuf, shapelessRecipe: ExtractorShapedRecipe)
                = SHAPELESS_SERIALIZER.write(packetByteBuf, shapelessRecipe.recipe)
    }

}