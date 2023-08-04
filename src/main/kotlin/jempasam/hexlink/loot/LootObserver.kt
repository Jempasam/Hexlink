package jempasam.hexlink.loot

import jempasam.hexlink.HexlinkMod
import net.fabricmc.fabric.api.loot.v2.LootTableEvents
import net.fabricmc.fabric.api.loot.v2.LootTableSource
import net.minecraft.loot.LootManager
import net.minecraft.loot.LootTable
import net.minecraft.resource.ResourceManager
import net.minecraft.server.MinecraftServer
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object LootObserver : LootTableEvents.Modify{
    fun register(){
        LootTableEvents.MODIFY.register(this)
    }

    override fun modifyLootTable(resourceManager: ResourceManager, lootManager: LootManager, id: Identifier, tableBuilder: LootTable.Builder, source: LootTableSource) {
        if(id==Identifier.of("minecraft","chests/simple_dungeon")){
            val focuses=lootManager.getTable(Identifier(HexlinkMod.MODID,"great_focuses"))
            for(pool in focuses.pools)tableBuilder.pool(pool)
        }
    }
}