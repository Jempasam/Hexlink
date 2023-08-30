package jempasam.hexlink

import assets.hexlink.config.HexlinkClientConfig
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import jempasam.hexlink.block.HexlinkBlockClient
import jempasam.hexlink.config.Config
import jempasam.hexlink.item.color.HexlinkColorProviders
import jempasam.hexlink.lens.HexlinkScryingLensDisplay
import jempasam.hexlink.model.predicate.HexlinkModelPredicates
import jempasam.hexlink.particle.HexlinkClientParticles
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.MinecraftClient

object HexlinkModClient : ClientModInitializer {

	fun config(config: Config, file: String){
		val gson=GsonBuilder().setPrettyPrinting().create()
		val client_config_file=MinecraftClient.getInstance().runDirectory.resolve("config/$file.json")
		if(client_config_file.exists()){
			client_config_file.reader().apply {
				val obj=gson.fromJson(this, JsonObject::class.java)
				close()
				if(obj!=null)config.load(obj)
			}
		}
		else client_config_file.createNewFile()

		client_config_file.writer().apply{
			gson.toJson(config.save(),this)
			close()
		}
	}
	override fun onInitializeClient() {
		// Config
		config(HexlinkClientConfig, "hexlink-client")

		HexlinkColorProviders.registerItemColors()
		HexlinkModelPredicates.registerItemPredicates()
		HexlinkBlockClient.registerBlockRender()
		HexlinkScryingLensDisplay.registerDisplays()
		HexlinkClientParticles
		//HudRenderCallback.EVENT.register(SpellHUD())
	}

}