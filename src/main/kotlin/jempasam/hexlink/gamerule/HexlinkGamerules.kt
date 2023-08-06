package jempasam.hexlink.gamerule

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry
import net.minecraft.world.GameRules.Category


object HexlinkGamerules {
    val EXTRACTION_PROBABILITY = GameRuleRegistry.register("spiritExtractionSuccessProb", Category.MISC, GameRuleFactory.createDoubleRule(0.01, 0.0, 1.0))
}