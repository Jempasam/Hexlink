package jempasam.hexlink.loot.function

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import jempasam.hexlink.utils.asJSON
import jempasam.hexlink.utils.asNBT
import net.minecraft.item.ItemStack
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.function.LootFunction
import net.minecraft.loot.function.LootFunctionType
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.JsonSerializer

class EasyNBTLootFunction(val nbt: NbtCompound) : LootFunction{
    override fun apply(t: ItemStack, u: LootContext): ItemStack {
        t.nbt=nbt.copy()
        return t
    }

    override fun getType(): LootFunctionType = Type

    object Type: LootFunctionType(Serializer)

    object Serializer: JsonSerializer<EasyNBTLootFunction>{
        override fun fromJson(json: JsonObject, context: JsonDeserializationContext): EasyNBTLootFunction {
            return EasyNBTLootFunction(
                    json.get("nbt").asJsonObject.asNBT()
            )
        }

        override fun toJson(json: JsonObject, obj: EasyNBTLootFunction, context: JsonSerializationContext) {
            json.apply {
                add("nbt",obj.nbt.asJSON())
            }
        }

    }
}