package jempasam.hexlink.recipe

import at.petrak.hexcasting.common.lib.HexItems
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import jempasam.hexlink.item.MixedPigmentItem
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.DyeItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.CraftingRecipe
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.registry.Registry
import net.minecraft.world.World

class MixedPigmentRecipe(private val id: Identifier, val result: MixedPigmentItem) : CraftingRecipe {
    override fun craft(inventory: CraftingInventory): ItemStack {
        println("TRY TO CRAFT")
        val dyes=mutableListOf<DyeColor>()
        var powder_count=0
        for(i in 0 until inventory.size()){
            val item=inventory.getStack(i).item
            if(item is DyeItem){
                dyes.add(item.color)
                if(dyes.size>2)return Items.ACACIA_STAIRS.defaultStack
            }
            else if(item==HexItems.AMETHYST_DUST){
                powder_count++
                if(powder_count>4)return Items.ACACIA_STAIRS.defaultStack
            }
        }
        if(dyes.size<1 || powder_count!=4)return Items.ACACIA_STAIRS.defaultStack
        val ret=result.defaultStack
        result.setColor1(ret,dyes.get(0).fireworkColor)
        if(dyes.size>1)result.setColor2(ret,dyes.get(1).fireworkColor)
        return ret
    }

    override fun matches(inventory: CraftingInventory, world: World): Boolean {
        println("TRY TO MATCH")
        var dyes_count=0
        var powder_count=0
        for(i in 0 until inventory.size()){
            val item=inventory.getStack(i).item
            if(item is DyeItem){
                dyes_count++
                if(dyes_count>2)return false
            }
            else if(item==HexItems.AMETHYST_DUST){
                powder_count++
                if(powder_count>4)return false
            }
        }
        return powder_count==4 && dyes_count>0
    }

    override fun fits(width: Int, height: Int): Boolean {
        println("TEST IF FIT")
        return width*height>=5
    }

    override fun getOutput(): ItemStack {
        println("GET OUTPUT")
        return result.defaultStack
    }

    override fun getId(): Identifier {
        return id
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return SERIALIZER
    }

    override fun getIngredients(): DefaultedList<Ingredient> {
        val ret=DefaultedList.of<Ingredient>()
        for(i in 0 until 4)ret.add(Ingredient.ofItems(HexItems.AMETHYST_DUST))
        ret.add(Ingredient.ofItems(Items.BLACK_DYE, Items.RED_DYE, Items.BLACK_DYE, Items.YELLOW_DYE, Items.GREEN_DYE, Items.PURPLE_DYE, Items.GRAY_DYE))
        return ret
    }

    object SERIALIZER: RecipeSerializer<MixedPigmentRecipe>{
        override fun read(id: Identifier, json: JsonObject): MixedPigmentRecipe {
            println("TRY TO SERIALIZE")
            try{
                val result_identifier=Identifier(json.get("result").asString)
                val result=Registry.ITEM.getOrEmpty(result_identifier).orElseThrow({JsonParseException(result_identifier.toString()+" does not exist")})
                if(!(result is MixedPigmentItem))throw JsonParseException(result_identifier.toString()+" does not a mixed pigment")
                return MixedPigmentRecipe(id, result)
            }catch (e: Exception){
                throw JsonParseException(e)
            }
        }

        override fun read(id: Identifier, buf: PacketByteBuf): MixedPigmentRecipe {
            try{
                val result_identifier=Identifier(buf.readString())
                val result=Registry.ITEM.getOrEmpty(result_identifier).orElseThrow({JsonParseException(result_identifier.toString()+" does not exist")})
                if(!(result is MixedPigmentItem))throw JsonParseException(result_identifier.toString()+" does not a mixed pigment")
                return MixedPigmentRecipe(id, result)
            }catch (e: Exception){
                throw JsonParseException(e)
            }
        }

        override fun write(buf: PacketByteBuf, recipe: MixedPigmentRecipe) {
            buf.writeString(Registry.ITEM.getId(recipe.result).toString())
        }

    }
}