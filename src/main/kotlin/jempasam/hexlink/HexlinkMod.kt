package jempasam.hexlink

import HexlinkIotas
import jempasam.hexlink.item.HexlinkItems
import jempasam.hexlink.loot.LootObserver
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object HexlinkMod : ModInitializer {
	val logger = LoggerFactory.getLogger("hexlink")
	val MODID = "hexlink"

	override fun onInitialize() {
		// Items
		HexlinkIotas.registerAll()
		HexlinkPatterns.registerAll()
		HexlinkItems.registerAll()
		LootObserver.register()
		logger.info("Hello Fabric world!")
	}
}