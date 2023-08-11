package jempasam.hexlink.loot.function

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.mojang.brigadier.StringReader
import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.HexlinkRegistry
import jempasam.hexlink.item.SpiritContainerItem
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.extracter.SpiritExtractor
import net.minecraft.item.ItemStack
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.function.LootFunction
import net.minecraft.loot.function.LootFunctionType
import net.minecraft.nbt.StringNbtReader
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.JsonSerializer

class SpiritExtractorLootFunction(val extractor: SpiritExtractor<*>?, val spirit: Spirit?) : LootFunction{
    override fun apply(t: ItemStack, u: LootContext): ItemStack {
        val item=t.item
        if(item is SpiritContainerItem){
            if(extractor!=null)item.setExtractor(t,extractor)
            if(spirit!=null)item.setSpirit(t,spirit)
        }
        return t
    }

    override fun getType(): LootFunctionType = Type

    object Type: LootFunctionType(Serializer)

    object Serializer: JsonSerializer<SpiritExtractorLootFunction>{
        override fun fromJson(json: JsonObject, context: JsonDeserializationContext): SpiritExtractorLootFunction {
            val extractor_id=JsonHelper.getString(json,"extractor", null)
            val extractor=extractor_id?.let{ HexlinkRegistry.SPIRIT_EXTRACTER.get(Identifier(it)) }

            val spirit_type_id=JsonHelper.getString(json,"spirit_type", null)
            val spirit_type=spirit_type_id?.let{ HexlinkRegistry.SPIRIT.get(Identifier(it)) }

            val spirit=if(spirit_type!=null){
                val nbt=StringNbtReader(StringReader(JsonHelper.getString(json, "spirit_value"))).parseElement()
                val ret=spirit_type.deserialize(nbt)
                if(ret==null) HexlinkMod.logger.error(nbt.toString()+" not a valid "+spirit_type_id)
                ret
            }
            else null

            return SpiritExtractorLootFunction(extractor,spirit)
        }

        override fun toJson(json: JsonObject, obj: SpiritExtractorLootFunction, context: JsonSerializationContext) {
            if(obj.extractor!=null)
                json.addProperty("extractor", HexlinkRegistry.SPIRIT_EXTRACTER.getId(obj.extractor).toString())

            if(obj.spirit!=null){
                json.addProperty("spirit_type", HexlinkRegistry.SPIRIT.getId(obj.spirit.getType()).toString())
                json.addProperty("spirit_value", obj.spirit.serialize().toString())
            }
        }

    }
}