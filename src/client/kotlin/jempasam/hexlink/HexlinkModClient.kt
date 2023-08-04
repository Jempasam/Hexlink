package jempasam.hexlink

import jempasam.hexlink.item.color.HexlinkColorProviders
import jempasam.hexlink.model.predicate.HexlinkModelPredicates
import net.fabricmc.api.ClientModInitializer

object HexlinkModClient : ClientModInitializer {
	override fun onInitializeClient() {
		HexlinkColorProviders.registerItemColors()
		HexlinkModelPredicates.registerItemPredicates()
		//HudRenderCallback.EVENT.register(SpellHUD())
	}
}