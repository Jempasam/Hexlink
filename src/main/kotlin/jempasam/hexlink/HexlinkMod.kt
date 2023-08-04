package jempasam.hexlink

import HexlinkIotas
import jempasam.hexlink.item.HexlinkItems
import jempasam.hexlink.loot.LootObserver
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory
import jempasam.hexlink.recipe.HexlinkRecipes
import jempasam.hexlink.trinkets.HexlinkTrinkets

object HexlinkMod : ModInitializer {
	val logger = LoggerFactory.getLogger("hexlink")
	val MODID = "hexlink"

	override fun onInitialize() {
		HexlinkIotas.registerAll()
		HexlinkPatterns.registerAll()
		HexlinkItems.registerAll()
		LootObserver.register()
		HexlinkTrinkets.registerTrinkets()
		HexlinkRecipes.registerRecipes()
	}
}