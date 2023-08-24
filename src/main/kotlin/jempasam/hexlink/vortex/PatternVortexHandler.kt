package jempasam.hexlink.vortex

import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import jempasam.hexlink.spirit.ItemSpirit
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.inout.SpiritHelper
import jempasam.hexlink.utils.asNBT
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.recipe.RecipeManager
import net.minecraft.recipe.RecipeType
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.JsonHelper

class PatternVortexHandler : AbstractVortexHandler{


    private val recipe_manager=RecipeManager.createCachedMatchGetter(RecipeType.CRAFTING)

    private val pattern: List<Slot>
    private val ingredient_count: Int
    private val material_count: Int
    private val multiplier: Float
    private val useDurability: Boolean


    constructor(catalyzer: List<Spirit>, output: List<Spirit>, pattern: List<Slot>, useDurability: Boolean, multiplier: Float)
        : super(catalyzer,output)
    {
        assert(pattern.size==3*3)
        this.pattern=pattern;
        this.ingredient_count=pattern.maxOf { it.id }+1
        this.material_count=pattern.maxOf { it.mat }+1
        this.useDurability=useDurability
        this.multiplier=multiplier
    }

    constructor(obj: JsonObject)
        : super(obj)
    {
        this.pattern=JsonHelper.getArray(obj,"pattern").let{
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
        }
        assert(this.pattern.size==3*3)
        this.ingredient_count=pattern.maxOf { it.id }+1
        this.material_count=pattern.maxOf { it.mat }+1
        this.useDurability=JsonHelper.getBoolean(obj,"use_durability", false)
        this.multiplier=JsonHelper.getFloat(obj, "multiplier", 1.0f)
    }


    override fun findRealRecipe(ingredients: List<Spirit>, world: ServerWorld): AbstractVortexHandler.Recipe? {
        if(ingredients.size >= ingredient_count){
            val items_ingredients=ingredients.map { SpiritHelper.asItem(it) }
            if(items_ingredients.all { it!=null }){
                val nonnull_items_ingredients=items_ingredients.map { it as Item }

                val materials= mutableListOf<Item?>()
                for(i in 0..<material_count)materials.add(null)
                val inventory=CraftingInventory(NONEHANDLE,3,3)
                for(i in 0..<3*3){
                    val stack=pattern[i].stack(nonnull_items_ingredients, materials)
                    stack ?: return null
                    inventory.setStack(i,stack)
                }
                val recipe=recipe_manager.getFirstMatch(inventory,world)
                if(recipe.isPresent){
                    val result=recipe.get().craft(inventory)
                    var count=Math.max(1,(result.count*multiplier).toInt())
                    if(useDurability && result.maxDamage>0)count*=result.maxDamage
                    if(!result.isEmpty)return Recipe(result.item, count, this, world)
                }
            }
        }
        return null
    }

    override fun getRealRecipesExamples(): Sequence<Pair<List<Spirit>, List<Spirit>>> = sequenceOf()

    class Recipe(val item: Item, val count: Int, val handler: PatternVortexHandler, val world: ServerWorld): AbstractVortexHandler.Recipe(handler){

        override fun realIngredientCount(): Int = handler.ingredient_count

         override fun realMix(ingredients: List<Spirit>): List<Spirit> {
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
        override fun serialize(json: JsonObject): PatternVortexHandler = PatternVortexHandler(json)
    }
}