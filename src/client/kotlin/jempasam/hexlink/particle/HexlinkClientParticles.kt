package jempasam.hexlink.particle

import jempasam.hexlink.HexlinkMod
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.PendingParticleFactory
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback
import net.minecraft.client.particle.SpellParticle
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleType
import net.minecraft.screen.PlayerScreenHandler
import net.minecraft.util.Identifier


object HexlinkClientParticles {
    init{
        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(this::registerSprites)
        create(HexlinkParticles.SPIRIT, SpellParticle::EntityFactory)
    }

    fun registerSprites(atlas: SpriteAtlasTexture, registry: ClientSpriteRegistryCallback.Registry){
        registry.register(Identifier(HexlinkMod.MODID, "particle/spirit1"))
        registry.register(Identifier(HexlinkMod.MODID, "particle/spirit2"))
        registry.register(Identifier(HexlinkMod.MODID, "particle/spirit3"))
        registry.register(Identifier(HexlinkMod.MODID, "particle/spirit4"))
        registry.register(Identifier(HexlinkMod.MODID, "particle/spirit5"))
        registry.register(Identifier(HexlinkMod.MODID, "particle/spirit6"))
        registry.register(Identifier(HexlinkMod.MODID, "particle/spirit7"))
    }

    fun <T: ParticleEffect>create(type: ParticleType<T>, factory: PendingParticleFactory<T>){
        ParticleFactoryRegistry.getInstance().register(type,factory)
    }


}