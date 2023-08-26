package jempasam.hexlink.loot.function

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import jempasam.hexlink.item.functionnality.ItemSpiritTarget
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.utils.addSpirit
import jempasam.hexlink.utils.getSpirit
import net.minecraft.item.ItemStack
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.function.LootFunction
import net.minecraft.loot.function.LootFunctionType
import net.minecraft.util.JsonHelper
import net.minecraft.util.JsonSerializer

class SpiritTargetLootFunction(val spirit: Spirit, val count: Int) : LootFunction{
    override fun apply(t: ItemStack, u: LootContext): ItemStack {
        val item=t.item
        if(item is ItemSpiritTarget){
            val target=item.getSpiritTarget(t)
            val flux=target.fill(count,spirit)
            flux.fill(flux.maxcount)
        }
        return t
    }

    override fun getType(): LootFunctionType = Type

    object Type: LootFunctionType(Serializer)

    object Serializer: JsonSerializer<SpiritTargetLootFunction>{
        override fun fromJson(json: JsonObject, context: JsonDeserializationContext): SpiritTargetLootFunction {
            return SpiritTargetLootFunction(
                    json.getSpirit("spirit"),
                    JsonHelper.getInt(json,"count",1)
            )
        }

        override fun toJson(json: JsonObject, obj: SpiritTargetLootFunction, context: JsonSerializationContext) {
            json.apply {
                addSpirit("spirit",obj.spirit)
                addProperty("count",obj.count)
            }
        }

    }
}