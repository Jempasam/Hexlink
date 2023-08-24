package jempasam.hexlink.vortex

import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import jempasam.hexlink.spirit.ItemSpirit
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.inout.SpiritHelper
import jempasam.hexlink.utils.asNBT
import jempasam.hexlink.utils.getSpirit
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.recipe.RecipeManager
import net.minecraft.recipe.RecipeType
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.JsonHelper
import kotlin.math.min

class PatternVortexHandler(val catlzer: Spirit, val pattern: List<Slot>, val use_durability: Boolean, val multiplier: Float) : CatalyzedVortexHandler{

    private val recipe_manager=RecipeManager.createCachedMatchGetter(RecipeType.CRAFTING)
    private val ingredient_count=pattern.maxOf { it.id }+1
    private val material_count=pattern.maxOf { it.mat }+1

    init{
        assert(pattern.size==3*3)
    }

    override fun getCatalyzer(): Spirit = catlzer

    override fun findRecipe(ingredients: List<Spirit>, world: ServerWorld): HexVortexHandler.Recipe? {
        if(ingredients.size >= 1+ingredient_count){
            val first=ingredients[0]
            val craft_ingredients=ingredients.subList(1,ingredients.size)
            val items_ingredients=craft_ingredients.map { SpiritHelper.asItem(it) }
            if(first==catlzer && items_ingredients.all { it!=null }){
                val items_ingredients=items_ingredients.map { it as Item }

                val materials= mutableListOf<Item?>()
                for(i in 0..<material_count)materials.add(null)
                val inventory=CraftingInventory(NONEHANDLE,3,3)
                for(i in 0..<3*3){
                    val stack=pattern[i].stack(items_ingredients, materials)
                    stack ?: return null
                    inventory.setStack(i,stack)
                }
                val recipe=recipe_manager.getFirstMatch(inventory,world)
                if(recipe.isPresent){
                    val result=recipe.get().craft(inventory)
                    var count=min(1,(result.count*multiplier).toInt())
                    if(use_durability && result.maxDamage>0)count*=result.maxDamage
                    if(!result.isEmpty)return Recipe(result.item, count, ingredient_count+1, world)
                }
            }
        }
        return null
    }

    class Recipe(val item: Item, val count: Int, val ingcount: Int, val world: ServerWorld): HexVortexHandler.Recipe{

        override fun ingredientCount(): Int = ingcount

         override fun mix(ingredients: List<Spirit>): List<Spirit> {
            return mutableListOf<Spirit>()
                    .also { for(i in 0..<count)it.add(ItemSpirit(item)) }
        }
    }

    interface Slot{
        val id: Int
        val mat: Int
        fun stack(items: List<Item>, mats: MutableList<Item?>): ItemStack?
    }

    class ItemStackSlot(val stack: ItemStack): Slot{
        override val id: Int=-1
        override val mat: Int=-1
        override fun stack(items: List<Item>, mats: MutableList<Item?>): ItemStack? = stack.copy()
    }

    class InputSlot(override val id: Int, override  val mat: Int): Slot{
        override fun stack(items: List<Item>, mats: MutableList<Item?>): ItemStack?{
            assert(id>=0 && id<items.size)
            if(mats[mat]==null){
                mats[mat]=items[id]
                return items[id].defaultStack
            }
            else if(mats[mat]==items[id])return items[id].defaultStack
            else return null
        }
    }

    class NoneSlot: Slot{
        override val id: Int=-1
        override val mat: Int=-1
        override fun stack(items: List<Item>, mats: MutableList<Item?>): ItemStack?{
            return ItemStack.EMPTY
        }
    }

    object NONEHANDLE: ScreenHandler(null,-1){
        override fun transferSlot(player: PlayerEntity?, index: Int): ItemStack
            = ItemStack.EMPTY

        override fun canUse(player: PlayerEntity?): Boolean
            = true

    }



    object SERIALIZER: HexVortexHandler.Serializer<PatternVortexHandler>{
        override fun serialize(json: JsonObject): PatternVortexHandler {
            return PatternVortexHandler(
                    json.getSpirit("catalyzer"),
                    JsonHelper.getArray(json,"pattern").let{
                        if(it.size()==3*3){
                            val ret= mutableListOf<Slot>()
                            for(json_slot in it){
                                if(json_slot.isJsonObject){
                                    val obj=json_slot.asJsonObject
                                    if(obj.size()==0)ret.add(NoneSlot())
                                    else{
                                        val input=obj.get("input")?.asInt
                                        if(input!=null){
                                            val mat=obj.get("mat")?.asInt ?: 0
                                            if(input<0)throw JsonParseException("Input slot input id below 0")
                                            if(mat<0)throw JsonParseException("Input slot mat id below 0")
                                            else ret.add(InputSlot(input,mat))
                                        }
                                        else ret.add(ItemStackSlot(ItemStack.fromNbt(json_slot.asJsonObject.asNBT())))
                                    }

                                }
                                else throw JsonParseException("Pattern slot not of object type")
                            }
                            ret
                        }
                        else throw JsonParseException("Pattern not of good size, need 9 slots")
                    },
                    JsonHelper.getBoolean(json,"use_durability", false),
                    JsonHelper.getFloat(json, "multiplier", 1.0f)
            )
        }
    }
}