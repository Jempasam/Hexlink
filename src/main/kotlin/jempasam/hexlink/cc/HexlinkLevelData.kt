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
    private val rankMap= mutableMapOf<UUID,LevelRanks.Rank>()
    private val reverse_rank_map= mutableMapOf<LevelRanks.Rank,UUID>()

    override fun ranks(): Collection<LevelRanks.Rank> = rankMap.values

    override fun getPlayer(rank: LevelRanks.Rank): UUID? = reverse_rank_map[rank]

    override fun getRank(player: UUID): LevelRanks.Rank? = rankMap[player]

    override fun setPlayer(rank: LevelRanks.Rank, player: UUID?) {
        val oldPlayer= reverse_rank_map[rank]
        if(oldPlayer==player)return
        if(oldPlayer!=null){
            rankMap.remove(oldPlayer)
        }
        if(player!=null){
            val oldRank= rankMap[player]
            if(oldRank!=null)reverse_rank_map.remove(rank)
            reverse_rank_map.put(rank,player)
            rankMap.put(player,rank)
        }
        else reverse_rank_map.remove(rank)
    }

    override fun setRank(player: UUID, rank: LevelRanks.Rank?) {
        val oldRank= rankMap[player]
        if(oldRank==rank)return
        if(oldRank!=null){
            rankMap.remove(player)
        }
        if(rank!=null){
            val oldPlayer= reverse_rank_map[rank]
            if(oldPlayer!=null)rankMap.remove(oldPlayer)
            rankMap.put(player, rank)
            reverse_rank_map.put(rank, player)
        }
        else rankMap.remove(player)
    }

    override fun clearRanks() {
        rankMap.clear()
        reverse_rank_map.clear()
    }

    override fun readFromNbt(tag: NbtCompound) {
        tag.getCompound("ranks")?.let{ readRanksFromNbt(it) }
    }

    override fun writeToNbt(tag: NbtCompound) {
        tag.put("ranks", NbtCompound().also{writeRanksToNbt(it)})
    }
}