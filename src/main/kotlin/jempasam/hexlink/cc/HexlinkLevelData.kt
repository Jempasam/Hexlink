package jempasam.hexlink.cc

import dev.onyxstudios.cca.api.v3.component.Component
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry
import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.world.LevelRanks
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import net.minecraft.world.WorldProperties
import java.util.*

class HexlinkLevelData(private val level: WorldProperties) : Component, LevelRanks{

    companion object{
        val KEY=ComponentRegistry.getOrCreate(Identifier(HexlinkMod.MODID,"world_data"),HexlinkLevelData::class.java)
    }


    // Ranks
    private val rank_map= mutableMapOf<UUID,LevelRanks.Rank>()
    private val reverse_rank_map= mutableMapOf<LevelRanks.Rank,UUID>()

    override fun ranks(): Collection<LevelRanks.Rank> = rank_map.values

    override fun getPlayer(rank: LevelRanks.Rank): UUID? = reverse_rank_map[rank]

    override fun getRank(player: UUID): LevelRanks.Rank? = rank_map[player]

    override fun setPlayer(rank: LevelRanks.Rank, player: UUID?) {
        val old_player= reverse_rank_map[rank]
        if(old_player==player)return
        if(old_player!=null){
            rank_map.remove(old_player)
        }
        if(player!=null){
            val old_rank= rank_map[player]
            if(old_rank!=null)reverse_rank_map.remove(rank)
            reverse_rank_map.put(rank,player)
            rank_map.put(player,rank)
        }
        else reverse_rank_map.remove(rank)
    }

    override fun setRank(player: UUID, rank: LevelRanks.Rank?) {
        val old_rank= rank_map[player]
        if(old_rank==rank)return
        if(old_rank!=null){
            rank_map.remove(player)
        }
        if(rank!=null){
            val old_player= reverse_rank_map[rank]
            if(old_player!=null)rank_map.remove(old_player)
            rank_map.put(player, rank)
            reverse_rank_map.put(rank, player)
        }
        else rank_map.remove(player)
    }

    override fun clearRanks() {
        rank_map.clear()
        reverse_rank_map.clear()
    }

    override fun readFromNbt(tag: NbtCompound) {
        tag.getCompound("ranks")?.let{ readRanksFromNbt(it) }
    }

    override fun writeToNbt(tag: NbtCompound) {
        tag.put("ranks", NbtCompound().also{writeRanksToNbt(it)})
    }
}