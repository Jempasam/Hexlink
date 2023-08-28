package jempasam.hexlink.recipe

import com.google.gson.JsonObject
import jempasam.hexlink.item.MixedPigmentItem
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.DyeItem
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.CraftingRecipe
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.ShapelessRecipe
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList
import net.minecraft.world.World

class MixedPigmentRecipe(val recipe: ShapelessRecipe): CraftingRecipe{
    override fun matches(craftingInventory: CraftingInventory, world: World): Boolean {
        if(!recipe.matches(craftingInventory, world))return false
        var counter=0
        for(i in 0 until craftingInventory.size()){
            if(craftingInventory.getStack(i).item is DyeItem)counter++
            if(counter>2)return false
        }
        return counter>0
    }

    override fun craft(craftingInventory: CraftingInventory): ItemStack {
        val ret=recipe.craft(craftingInventory)
        val retItem=ret.item
        if(retItem is MixedPigmentItem){
            val colors= mutableListOf<Int>()
            for(i in 0 until craftingInventory.size()){
                val stack=craftingInventory.getStack(i)
                val item=stack.item
                if(item is DyeItem)colors.add(item.color.fireworkColor)
            }
            if(colors.size>0)retItem.setColor1(ret, colors[0])
            if(colors.size>1)retItem.setColor2(ret, colors[1])
            else retItem.setColor2(ret, colors[0])
        }
        return ret
    }

    override fun getId(): Identifier = recipe.id

    override fun fits(width: Int, height: Int): Boolean = recipe.fits(width,height)

    override fun getOutput(): ItemStack = recipe.output

    override fun getIngredients(): DefaultedList<Ingredient> = recipe.ingredients

    override fun getGroup(): String = recipe.group

    override fun getSerializer(): RecipeSerializer<*> = SERIALIZER


    object SERIALIZER : RecipeSerializer<MixedPigmentRecipe> {
        val SHAPELESS_SERIALIZER=ShapelessRecipe.Serializer()
        override fun read(identifier: Identifier, jsonObject: JsonObject): MixedPigmentRecipe
            = MixedPigmentRecipe(SHAPELESS_SERIALIZER.read(identifier,jsonObject))

        override fun read(identifier: Identifier, packetByteBuf: PacketByteBuf): MixedPigmentRecipe
            = MixedPigmentRecipe(SHAPELESS_SERIALIZER.read(identifier,packetByteBuf))

        override fun write(packetByteBuf: PacketByteBuf, shapelessRecipe: MixedPigmentRecipe)
            = SHAPELESS_SERIALIZER.write(packetByteBuf, shapelessRecipe.recipe)
    }

}