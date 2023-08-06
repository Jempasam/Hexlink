package jempasam.hexlink

import HexlinkIotas
import jempasam.hexlink.gamerule.HexlinkGamerules
import jempasam.hexlink.item.HexlinkItems
import jempasam.hexlink.loot.LootObserver
import jempasam.hexlink.recipe.HexlinkRecipes
import jempasam.hexlink.trinkets.HexlinkTrinkets
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

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
		HexlinkGamerules.EXTRACTION_PROBABILITY
	}
}