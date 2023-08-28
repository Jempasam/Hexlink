package jempasam.hexlink.loot.function

import jempasam.hexlink.HexlinkMod
import net.minecraft.loot.function.LootFunctionType
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object HexlinkLootFunctions {

    private fun register(id: String, lootFunction: LootFunctionType){
        Registry.register(Registry.LOOT_FUNCTION_TYPE, Identifier(HexlinkMod.MODID, id), lootFunction)
    }
    fun registerLootFunctions(){
        register("spirit", SpiritTargetLootFunction.Type)
        register("easy_nbt", EasyNBTLootFunction.Type)
    }
}