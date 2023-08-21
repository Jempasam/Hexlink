package jempasam.hexlink.loot

import jempasam.hexlink.HexlinkMod
import net.fabricmc.fabric.api.loot.v2.LootTableEvents
import net.fabricmc.fabric.api.loot.v2.LootTableSource
import net.minecraft.loot.LootManager
import net.minecraft.loot.LootTable
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier

object LootObserver : LootTableEvents.Modify{
    fun register(){
        LootTableEvents.MODIFY.register(this)
    }

    private val magic_loots= mapOf(
            Identifier.of("minecraft","chests/simple_dungeon") to 1,
            Identifier.of("minecraft","chests/desert_pyramid") to 1,
            Identifier.of("minecraft","chests/stronghold_library") to 3,
            Identifier.of("minecraft","chests/village_temple") to 3,
            Identifier.of("minecraft","chests/woodland_mansion") to 2
    )
    override fun modifyLootTable(resourceManager: ResourceManager, lootManager: LootManager, id: Identifier, tableBuilder: LootTable.Builder, source: LootTableSource) {
        val count= magic_loots[id]
        val focuses=lootManager.getTable(Identifier(HexlinkMod.MODID,"magic_loots"))
        if(count!=null){
            for(i in 0 until count){
                for(pool in focuses.pools)tableBuilder.pool(pool)
            }
        }
    }
}