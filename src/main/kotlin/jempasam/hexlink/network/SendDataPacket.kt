package jempasam.hexlink.network

import com.google.gson.Gson
import com.google.gson.JsonObject
import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.HexlinkRegistry
import jempasam.hexlink.spirit.BlockSpirit
import jempasam.hexlink.spirit.SpecialSpirit
import jempasam.hexlink.spirit.extractor.NodeExtractor
import jempasam.hexlink.spirit.extractor.node.ResultExtNode
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
            buffer.writeCollection(extractor.colors){ buf,it -> buf.writeInt(it)}
            buffer.writeInt(extractor.getCost())
            buffer.writeInt(extractor.duration)
        }

        // Special Spirits
        buffer.writeVarInt(HexlinkRegistry.SPECIAL_SPIRIT.size())
        HexlinkRegistry.SPECIAL_SPIRIT.entrySet.forEach { (id, spirit) ->
            buffer.writeIdentifier(id.value)
            buffer.writeText(spirit.text)
            buffer.writeInt(spirit.color)
        }

        // Vortex Recipes
        buffer.writeVarInt(HexlinkRegistry.HEXVORTEX_HANDLER.size())
        HexlinkRegistry.HEXVORTEX_HANDLER.entrySet.forEach { (id,recipe)->
            val parserid=HexlinkRegistry.HEXVORTEX_HANDLER_PARSER.getId(recipe.parser) ?: return@forEach
            buffer.writeIdentifier(id.value)
            buffer.writeIdentifier(parserid)
            val obj=JsonObject()
            recipe.serialize(obj)
            buffer.writeString(Gson().toJson(obj))
        }

        ServerPlayNetworking.send(player, DATASYNC_ID, buffer)
    }

    fun receiveData(buf: PacketByteBuf){
        HexlinkRegistry.EXTRACTOR.clear()
        val extractorCount=buf.readVarInt()
        for(i in 0 ..< extractorCount){
            val id=buf.readIdentifier()
            val extractor=NodeExtractor.of(
                buf.readText(),
                buf.readList { buf.readInt() },
                buf.readInt(),
                buf.readInt(),
                ResultExtNode(0,0f,null)
            )
                //StubExtractor(buf.readText(), buf.readInt(), buf.readInt())
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

        // Vortex Recipes
        val vortex_recipe_count=buf.readVarInt()
        HexlinkRegistry.HEXVORTEX_HANDLER.clear()
        for(i in 0 ..< vortex_recipe_count){
            val id=buf.readIdentifier() // Recipe Id
            val parser_id=buf.readIdentifier() // Parser Id
            val parser=HexlinkRegistry.HEXVORTEX_HANDLER_PARSER[parser_id]
            val str=buf.readString()
            runCatching {
                val json=Gson().fromJson(str, JsonObject::class.java)
                if (parser != null) HexlinkRegistry.HEXVORTEX_HANDLER.register(id, parser.parse(json))
            }
        }
        HexlinkRegistry.HEXVORTEX_HANDLER.lock()
        HexlinkMod.logger.info("Hexlink synchronized with server data.")
    }

    init {
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register{ player: ServerPlayerEntity, b: Boolean ->
            HexlinkMod.logger.debug("Sending data to ${player.name} ${player.server.isDedicated} $b")
            if(b)sendData(player)
        }
    }
}