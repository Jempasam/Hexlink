package jempasam.hexlink

import com.mojang.serialization.Lifecycle
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.extracter.SpiritExtractor
import jempasam.hexlink.vortex.HexVortexHandler
import jempasam.hexlink.world.LevelRanks
import net.minecraft.util.Identifier
import net.minecraft.util.registry.MutableRegistry
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.util.registry.SimpleRegistry

object HexlinkRegistry {
    val SPIRIT_KEY=registry_id<Spirit.SpiritType<*>>("spirit")
    val SPIRIT=registry(SPIRIT_KEY)

    val SPIRIT_EXTRACTER_KEY=registry_id<SpiritExtractor<*>>("spirit_extracter")
    val SPIRIT_EXTRACTER=registry(SPIRIT_EXTRACTER_KEY)

    val VORTEX_RECIPE_KEY=registry_id<SpiritExtractor<*>>("vortex_recipes")
    val VORTEX_RECIPE=registry(VORTEX_RECIPE_KEY)

    val HEXVORTEX_HANDLER_KEY= registry_id<HexVortexHandler>("hexvortex_handler")
    val HEXVORTEX_HANDLER= registry(HEXVORTEX_HANDLER_KEY)

    val RANK_KEY=registry_id<LevelRanks.Rank>("mage_rank")
    val RANK= registry(RANK_KEY)

    private fun <T>registry_id(id: String): RegistryKey<Registry<T>>{
        return RegistryKey.ofRegistry<T>(Identifier(HexlinkMod.MODID,id))
    }

    @SuppressWarnings("unchecked-cast")
    private fun <T>registry(key: RegistryKey<Registry<T>>): SimpleRegistry<T>{
        val registry=SimpleRegistry(key, Lifecycle.stable(), null)
        val reg_registry=Registry.REGISTRIES as MutableRegistry<MutableRegistry<*>>
        Registry.register(reg_registry, key as RegistryKey<MutableRegistry<*>>, registry)
        return registry
    }
}