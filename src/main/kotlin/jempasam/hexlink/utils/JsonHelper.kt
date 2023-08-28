package jempasam.hexlink.utils

import com.google.gson.*
import com.mojang.brigadier.StringReader
import jempasam.hexlink.spirit.Spirit
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.StringNbtReader
import net.minecraft.recipe.AbstractCookingRecipe
import net.minecraft.recipe.RecipeType
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.registry.Registry

fun JsonObject.asNBT(): NbtCompound{
    val ret=NbtCompound()
    for(e in this.entrySet()){
        ret.put(e.key,e.value.asNBT())
    }
    return ret
}

fun JsonArray.asNBT(): NbtList{
    val ret=NbtList()
    for(e in this){
        ret.add(e.asNBT())
    }
    return ret
}

fun JsonElement.asNBT(): NbtElement{
    if(this.isJsonObject)return (this as JsonObject).asNBT()
    else if(this.isJsonArray)return (this as JsonArray).asNBT()
    else return PublicStringNbtReader(AcceptAllStringReader(this.asString)).parseElementPrimitive()
}



fun NbtCompound.asJSON(): JsonObject{
    val ret=JsonObject()
    for(key in this.keys){
        this.get(key)?.let { ret.add(key,it.asJSON()) }
    }
    return ret
}

fun NbtList.asJSON(): JsonArray{
    val ret=JsonArray()
    for(e in this){
        ret.add(e.asJSON())
    }
    return ret
}

fun NbtElement.asJSON(): JsonElement{
    return when (this) {
        is NbtCompound -> this.asJSON()
        is NbtList -> this.asJSON()
        else -> JsonPrimitive(this.asString())
    }
}

fun JsonObject.getSpirit(name: String): Spirit {
    return NbtHelper.readSpirit(JsonHelper.getObject(this, name).asNBT())
            ?: throw JsonParseException("Invalid Spirit")
}

fun JsonObject.addSpirit(name: String, spirit: Spirit){
    this.add(name, NbtHelper.writeSpirit(spirit).asJSON())
}

fun JsonElement.asSpirit(): Spirit {
    val obj=this.asJsonObject
    return NbtHelper.readSpirit(obj.asNBT())
            ?: throw JsonParseException("Invalid Spirit")
}

fun Spirit.toJSON(): JsonElement{
    return NbtHelper.writeSpirit(this).asJSON()
}

@Suppress("UNCHECKED_CAST")
fun JsonObject.getCookingRecipeType(name: String): RecipeType<AbstractCookingRecipe>{
    val recipe=Registry.RECIPE_TYPE.get(Identifier(JsonHelper.getString(this,name)))
    recipe ?: throw JsonParseException("No such recipe type of furnace")
    return recipe as RecipeType<AbstractCookingRecipe>
}

fun JsonObject.addCookingRecipeType(name: String, recipeType: RecipeType<AbstractCookingRecipe>){
    val id=Registry.RECIPE_TYPE.getKey(recipeType)
    if(id!=null){
        this.addProperty(name,id.toString())
    }
}


class AcceptAllStringReader(reader: String): StringReader(reader){
    override fun readUnquotedString(): String {
        val start = cursor
        while (canRead()) {
            skip()
        }
        return string.substring(start, cursor)
    }
}

class PublicStringNbtReader(reader: StringReader): StringNbtReader(reader){
    public override fun parseElementPrimitive(): NbtElement {
        return super.parseElementPrimitive()
    }
}

fun <T>jsonArray(list: List<T>, serializer: (T)->JsonElement): JsonArray{
    val ret=JsonArray()
    for(element in list){
        ret.add(serializer(element))
    }
    return ret
}

fun <T>JsonArray.read(deserializer: (JsonElement)->T): List<T>{
    val ret= mutableListOf<T>()
    for(element in this){
        ret.add(deserializer(element))
    }
    return ret
}