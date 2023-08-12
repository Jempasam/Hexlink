package jempasam.hexlink

import HexlinkIotas
import jempasam.hexlink.command.HexlinkCommands
import jempasam.hexlink.data.HexlinkDataLoaders
import jempasam.hexlink.gamerule.HexlinkGamerules
import jempasam.hexlink.item.HexlinkItems
import jempasam.hexlink.loot.LootObserver
import jempasam.hexlink.loot.function.HexlinkLootFunctions
import jempasam.hexlink.recipe.HexlinkRecipes
import jempasam.hexlink.spirit.HexlinkSpirits
import jempasam.hexlink.trinkets.HexlinkTrinkets
import jempasam.hexlink.world.LevelRanks
import net.fabricmc.api.ModInitializer
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import org.slf4j.LoggerFactory

object HexlinkMod : ModInitializer {
	val logger = LoggerFactory.getLogger("hexlink")
	const val MODID = "hexlink"

	override fun onInitialize() {
		logger.info("Hexlink started!")
		HexlinkIotas.registerAll()
		HexlinkPatterns.registerAll()
		HexlinkItems.registerAll()
		LootObserver.register()
		HexlinkTrinkets.registerTrinkets()
		HexlinkRecipes.registerRecipes()
		HexlinkLootFunctions.registerLootFunctions()
		HexlinkCommands.registerCommands()

		HexlinkGamerules
		HexlinkSpirits

		HexlinkDataLoaders.registerLoaders()

		Registry.register(HexlinkRegistry.RANK, Identifier(HexlinkMod.MODID,"testrank"), LevelRanks.Rank(0.1f, 10.0f,DyeColor.YELLOW.fireworkColor))

	}
}