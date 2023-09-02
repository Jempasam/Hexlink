package jempasam.hexlink.spirit.extractor

import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import jempasam.hexlink.HexlinkRegistry
import jempasam.hexlink.spirit.Spirit
import net.minecraft.entity.Entity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper

class SpecialExtractor(private val name: Text, private val color: Int): SpiritExtractor<Spirit> {

    private val node=Node()


    override fun extract(caster: ServerPlayerEntity?, target: Entity): SpiritExtractor.ExtractionResult<Spirit> {
        val result= find(caster,target) ?: return SpiritExtractor.noResult()
        val spirit=result.create()
        return SpiritExtractor.ExtractionResult(spirit.first,spirit.second){
            target.kill()
        }
    }

    override fun getColor(): Int = color

    override fun getName(): Text = name

    fun find(caster: ServerPlayerEntity?, entity: Entity): ExtractionRecipe.Result? = node.find(caster,entity)

    fun add(recipe: ExtractionRecipe) = node.add(recipe.conditions().toList(), 0, recipe)


    private class Node{
        val childs= mutableMapOf<ExtractionCondition, Node>()
        val recipes= mutableListOf<ExtractionRecipe>()

        fun find(caster: ServerPlayerEntity?, entity: Entity): ExtractionRecipe.Result?{
            for(child in childs){
                if(child.key.test(caster,entity)){
                    val result=child.value.find(caster,entity)
                    if(result!=null)return result
                }
            }
            for(recipe in recipes){
                val result=recipe.get(caster,entity)
                if(result!=null)return result
            }
            return null
        }

        fun add(conditions: List<ExtractionCondition>, start: Int, recipe: ExtractionRecipe){
            if(start>=conditions.size){
                recipes.add(recipe)
            }
            else{
                val child=childs.getOrPut(conditions[start],::Node)
                child.add(conditions, start+1, recipe)
            }
        }
    }

    companion object{
        fun parse(obj: JsonObject): SpecialExtractor{
            val ret=SpecialExtractor(
                    obj.get("name")?.let { Text.Serializer.fromJson(it) } ?: Text.of("INVALIDNAME"),
                    obj.get("color")?.asInt ?: 0x000000
            )
            for(recipeJson in JsonHelper.getArray(obj,"recipes")){
                val recipeObj=recipeJson.asJsonObject
                val typeId=JsonHelper.getString(recipeObj, "type")
                val serializer=HexlinkRegistry.EXTRACTION_RECIPE_SERIALIZER.get(Identifier(typeId))
                        ?: throw JsonParseException("Invalid recipe type \"$typeId\"")
                ret.add(serializer.parse(recipeObj))
            }
            return ret
        }

    }
}