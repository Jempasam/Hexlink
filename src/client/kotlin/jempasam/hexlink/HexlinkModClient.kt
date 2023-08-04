package jempasam.hexlink

import net.fabricmc.api.ClientModInitializer
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import jempasam.hexlink.hud.SpellHUD
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient

object HexlinkModClient : ClientModInitializer {
	override fun onInitializeClient() {
		HudRenderCallback.EVENT.register(SpellHUD())
	}
}