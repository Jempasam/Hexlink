package jempasam.hexlink.world

import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.HexlinkRegistry
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtHelper
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.util.*

interface LevelRanks {

    fun getRank(player: UUID): Rank?
    fun setRank(player: UUID, rank: Rank?)

    fun getPlayer(rank: Rank): UUID?
    fun setPlayer(rank: Rank, player: UUID?)

    fun clearRanks()

    fun ranks(): Collection<Rank>

    class Rank(val manaCost: Float, val artifactBattery: Float, val color: Int){

        fun getName(): Text
            = HexlinkRegistry.RANK.getId(this)
                    ?.let{ Text.translatable("rank."+it.toTranslationKey()).setStyle(Style.EMPTY.withColor(color))}
                    ?: Text.translatable("invalid.rank")
    }

    fun readRanksFromNbt(tag: NbtCompound) {
        clearRanks()
        for(key in tag.keys){
            try{
                val rank=HexlinkRegistry.RANK.get(Identifier(key))
                        ?: throw IllegalArgumentException("No rank associated to this id")

                val player=(tag.get(key) ?: throw IllegalArgumentException("Weird error"))
                        .let { NbtHelper.toUuid(it) }

                setRank(player,rank)
            }catch (e: Exception){
                HexlinkMod.logger.error("While loading rank \"$key\": "+e.message)
            }
        }
    }

    fun writeRanksToNbt(tag: NbtCompound) {
        for(rank in ranks()){
            val player=getPlayer(rank)
            val id=HexlinkRegistry.RANK.getId(rank)
            if(player!=null && id!=null)
                tag.put(id.toString(), NbtHelper.fromUuid(player))
        }
    }
}