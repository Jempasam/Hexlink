package jempasam.hexlink

import jempasam.hexlink.block.HexlinkBlocks
import jempasam.hexlink.command.HexlinkCommands
import jempasam.hexlink.data.HexlinkDataLoaders
import jempasam.hexlink.entity.HexlinkEntities
import jempasam.hexlink.gamerule.HexlinkGamerules
import jempasam.hexlink.iota.HexlinkIotas
import jempasam.hexlink.item.HexlinkItems
import jempasam.hexlink.loot.LootObserver
import jempasam.hexlink.loot.function.HexlinkLootFunctions
import jempasam.hexlink.particle.HexlinkParticles
import jempasam.hexlink.recipe.HexlinkRecipes
import jempasam.hexlink.recipe.vortex.HexVortexHandlers
import jempasam.hexlink.spirit.HexlinkSpirits
import jempasam.hexlink.spirit.extractor.node.ExtNodeParsers
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
		HexlinkParticles

		HexlinkIotas.registerAll()
		HexlinkPatterns.registerAll()
		HexlinkItems.registerAll()
		HexlinkBlocks.registerBlocks()
		LootObserver.register()
		HexlinkRecipes.registerRecipes()
		HexlinkLootFunctions.registerLootFunctions()
		HexlinkCommands.registerCommands()

		HexlinkGamerules
		HexlinkSpirits
		HexlinkEntities
		HexVortexHandlers
		ExtNodeParsers

		HexlinkDataLoaders

		Registry.register(HexlinkRegistry.RANK, Identifier(MODID,"testrank"), LevelRanks.Rank(0.1f, 10.0f,DyeColor.YELLOW.fireworkColor))

	}
}