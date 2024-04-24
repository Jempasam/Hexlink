package jempasam.hexlink.network

import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.HexlinkRegistry
import jempasam.hexlink.spirit.BlockSpirit
import jempasam.hexlink.spirit.SpecialSpirit
import jempasam.hexlink.spirit.extractor.StubExtractor
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.block.Blocks
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

object SendDataPacket {

    val DATASYNC_ID = Identifier(HexlinkMod.MODID, "data_sync")

    fun sendData(player: ServerPlayerEntity){
        val buffer=PacketByteBufs.create()

        // Extractor
        buffer.writeVarInt(HexlinkRegistry.EXTRACTOR.size())
        HexlinkRegistry.EXTRACTOR.entrySet.forEach { (id, extractor) ->
            buffer.writeIdentifier(id.value)
            buffer.writeText(extractor.getName())
            buffer.writeInt(extractor.getColor())
            buffer.writeInt(extractor.getCost())
        }

        // Special Spirits
        buffer.writeVarInt(HexlinkRegistry.SPECIAL_SPIRIT.size())
        HexlinkRegistry.SPECIAL_SPIRIT.entrySet.forEach { (id, spirit) ->
            buffer.writeIdentifier(id.value)
            buffer.writeText(spirit.text)
            buffer.writeInt(spirit.color)
        }

        ServerPlayNetworking.send(player, DATASYNC_ID, buffer)
    }

    fun receiveData(buf: PacketByteBuf){
        HexlinkRegistry.EXTRACTOR.clear()
        val extractorCount=buf.readVarInt()
        for(i in 0 ..< extractorCount){
            val id=buf.readIdentifier()
            val extractor=StubExtractor(buf.readText(), buf.readInt(), buf.readInt())
            HexlinkRegistry.EXTRACTOR.register(id, extractor)
        }
        HexlinkRegistry.EXTRACTOR.lock()

        HexlinkRegistry.SPECIAL_SPIRIT.clear()
        val spiritCount=buf.readVarInt()
        for(i in 0 ..< spiritCount){
            val id=buf.readIdentifier()
            val spirit=SpecialSpirit.SpecialType(
                BlockSpirit(Blocks.AIR), BlockSpirit(Blocks.AIR), BlockSpirit(Blocks.AIR), BlockSpirit(Blocks.AIR),
                buf.readText(), buf.readInt()
            )
            HexlinkRegistry.SPECIAL_SPIRIT.register(id, spirit)
        }
        HexlinkRegistry.SPECIAL_SPIRIT.lock()
    }

    init {
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register{ player: ServerPlayerEntity, b: Boolean ->
            println("Sending data to ${player.name} ${player.server.isDedicated} ${b}")
            if(b)sendData(player)
        }
    }
}