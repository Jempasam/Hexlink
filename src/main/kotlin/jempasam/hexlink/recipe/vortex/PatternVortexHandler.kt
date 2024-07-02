package jempasam.hexlink.recipe.vortex

import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import jempasam.hexlink.recipe.vortex.BurningVortexHandler.PARSER
import jempasam.hexlink.spirit.ItemSpirit
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.inout.SpiritHelper
import jempasam.hexlink.utils.asNBT
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RecipeManager
import net.minecraft.recipe.RecipeType
import net.minecraft.recipe.ShapedRecipe
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.JsonHelper
import kotlin.math.max

class PatternVortexHandler : AbstractVortexHandler {


    private val recipe_manager=RecipeManager.createCachedMatchGetter(RecipeType.CRAFTING)

    private val pattern: List<Slot>
    private val ingredientCount: Int
    private val materialCount: Int
    private val multiplier: Float
    private val useDurability: Boolean
    private val width: Int
    private val height: Int
    private val json: JsonObject


    constructor(catalyzer: List<Spirit>, output: List<Spirit>, pattern: List<Slot>, width: Int, height: Int, useDurability: Boolean, multiplier: Float)
        : super(catalyzer,output)
    {
        assert(pattern.size==3*3)
        this.pattern=pattern
        this.ingredientCount=pattern.maxOf { it.id }+1
        this.materialCount=pattern.maxOf { it.mat }+1
        this.useDurability=useDurability
        this.multiplier=multiplier
        this.width=width
        this.height=height
        this.json=JsonObject()
    }

    constructor(obj: JsonObject) : super(obj)
    {
        var width=-1
        this.pattern=mutableListOf()
        val patternLines=JsonHelper.getArray(obj,"pattern")
        for(line in patternLines){
            val line=line.asJsonArray
            if(width==-1)width=line.size()
            else if(width!=line.size())throw JsonParseException("Invalid pattern shape")
            for(slot in line){
                val slot=slot.asJsonObject
                if(slot.size()==0)pattern.add(NoneSlot())
                else{
                    val input=slot.get("input")?.asInt
                    if(input!=null){
                        val mat=slot.get("mat")?.asInt ?: 0
                        if(input<0)throw JsonParseException("Input slot input id below 0")
                        if(mat<0)throw JsonParseException("Input slot mat id below 0")
                        else pattern.add(InputSlot(input,mat))
                    }
                    else pattern.add(ItemStackSlot(ItemStack.fromNbt(slot.asNBT()).apply { count=1 }))
                }
            }
        }
        this.width=width
        this.height=patternLines.size()
        this.ingredientCount=pattern.maxOf { it.id }+1
        this.materialCount=pattern.maxOf { it.mat }+1
        this.useDurability=JsonHelper.getBoolean(obj,"use_durability", false)
        this.multiplier=JsonHelper.getFloat(obj, "multiplier", 1.0f)
        this.json=obj
    }

    override fun serialize(json: JsonObject) {
        this.json.entrySet().forEach{ json.add(it.key,it.value)}
    }

    override fun findRealRecipe(ingredients: Collection<Spirit>, world: ServerWorld): AbstractVortexHandler.Recipe? {
        if(ingredients.size >= ingredientCount){
            val itemsIngredients=ingredients.map { SpiritHelper.asItem(it) }
            if(itemsIngredients.all { it!=null }){
                val nonnullItemsIngredients=itemsIngredients.map { it as Item }

                val materials= MutableList<Item?>(materialCount) { null }
                val inventory=CraftingInventory(NONEHANDLE,width,height)
                for(i in 0..<width*height){
                    val stack=pattern[i].stack(nonnullItemsIngredients, materials)
                    stack ?: return null
                    inventory.setStack(i,stack)
                }
                val recipe=recipe_manager.getFirstMatch(inventory,world)
                if(recipe.isPresent){
                    val result=recipe.get().craft(inventory)
                    var count= max(1,(result.count*multiplier).toInt())
                    if(useDurability && result.maxDamage>0)count*=result.maxDamage
                    if(!result.isEmpty)return Recipe(result.item, count, this, world)
                }
            }
        }
        return null
    }

    override fun getRealRecipesExamples(manager: RecipeManager): Sequence<Pair<List<HexVortexHandler.Ingredient>, List<Spirit>>>{
        return sequence {
            recipe_loop@ for (recipe in manager.listAllOfType(RecipeType.CRAFTING)) {
                val materials = MutableList<Ingredient?>(materialCount) { null }
                val input = MutableList<Ingredient?>(ingredientCount) { null }
                val ingredients = recipe.ingredients

                if(recipe is ShapedRecipe && recipe.width==width && recipe.height==height){
                    for (i in pattern.indices) {
                        val ingredient = ingredients[i]
                        val slot = pattern.get(i)
                        if(!slot.test(ingredient, input, materials))continue@recipe_loop
                    }
                }
                else if(recipe !is ShapedRecipe && ingredients.size<=pattern.size){
                    for(i in 0..<ingredients.size){
                        val ingredient = ingredients[i]
                        val slot = pattern.get(i)
                        if(!slot.test(ingredient, input, materials))continue@recipe_loop
                    }
                    for(i in ingredients.size..<pattern.size){
                        val slot = pattern.get(i)
                        if(!slot.test(Ingredient.EMPTY, input, materials))continue@recipe_loop
                    }
                }
                else continue@recipe_loop

                val count= max(1,(recipe.output.count*multiplier).toInt())
                yield(
                        input.map { HexVortexHandler.Ingredient(it) }
                                to List(count) { SpiritHelper.asSpirit(recipe.output.item) }
                )
            }
        }
    }

    class Recipe(val item: Item, val count: Int, val handler: PatternVortexHandler, val world: ServerWorld): AbstractVortexHandler.Recipe(handler){

        override fun realIngredientCount(): Int = handler.ingredientCount

         override fun realMix(ingredients: Collection<Spirit>): List<Spirit> {
            return mutableListOf<Spirit>()
                    .also { for(i in 0..<count)it.add(ItemSpirit(item)) }
        }
    }

    interface Slot{
        val id: Int
        val mat: Int
        fun stack(items: List<Item>, mats: MutableList<Item?>): ItemStack?
        fun test(stack: Ingredient, inputs: MutableList<Ingredient?>, mats: MutableList<Ingredient?>): Boolean
    }

    class ItemStackSlot(val stack: ItemStack): Slot {
        override val id: Int=-1
        override val mat: Int=-1
        override fun stack(items: List<Item>, mats: MutableList<Item?>): ItemStack? = stack.copy()
        override fun test(stack: Ingredient, inputs: MutableList<Ingredient?>, mats: MutableList<Ingredient?>): Boolean{
            return stack.test(this.stack)
        }
    }

    class InputSlot(override val id: Int, override  val mat: Int): Slot {
        override fun stack(items: List<Item>, mats: MutableList<Item?>): ItemStack?{
            assert(id>=0 && id<items.size)
            if(mats[mat]==null){
                mats[mat]=items[id]
                return items[id].defaultStack
            }
            else if(mats[mat]==items[id])return items[id].defaultStack
            else return null
        }

        override fun test(stack: Ingredient, inputs: MutableList<Ingredient?>, mats: MutableList<Ingredient?>): Boolean{
            if((mats[mat]===null || stack==mats[mat]) && !stack.isEmpty){
                mats[mat]=stack
                inputs[id]=stack
                return true
            }
            else return false
        }
    }

    class NoneSlot: Slot {
        override val id: Int=-1
        override val mat: Int=-1
        override fun stack(items: List<Item>, mats: MutableList<Item?>): ItemStack? = ItemStack.EMPTY
        override fun test(stack: Ingredient, inputs: MutableList<Ingredient?>, mats: MutableList<Ingredient?>): Boolean = stack.isEmpty
    }

    object NONEHANDLE: ScreenHandler(null,-1){
        override fun transferSlot(player: PlayerEntity?, index: Int): ItemStack
            = ItemStack.EMPTY

        override fun canUse(player: PlayerEntity?): Boolean
            = true

    }

    override val parser get() = PARSER

    object PARSER: HexVortexHandler.Parser<PatternVortexHandler> {
        override fun parse(json: JsonObject): PatternVortexHandler = PatternVortexHandler(json)
    }
}