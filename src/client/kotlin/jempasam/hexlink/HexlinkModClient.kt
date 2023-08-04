package jempasam.hexlink

import jempasam.hexlink.item.color.HexlinkColorProviders
import net.fabricmc.api.ClientModInitializer

object HexlinkModClient : ClientModInitializer {
	override fun onInitializeClient() {
		HexlinkColorProviders.registerItemColors()
		//HudRenderCallback.EVENT.register(SpellHUD())
	}
}