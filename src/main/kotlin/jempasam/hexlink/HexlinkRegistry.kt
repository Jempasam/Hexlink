package jempasam.hexlink

import com.mojang.serialization.Lifecycle
import jempasam.hexlink.spirit.SpecialSpirit
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.extracter.SpiritExtractor
import jempasam.hexlink.vortex.HexVortexHandler
import jempasam.hexlink.world.LevelRanks
import net.minecraft.util.Identifier
import net.minecraft.util.registry.MutableRegistry
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.util.registry.SimpleRegistry
import java.util.*

object HexlinkRegistry {
    val SPIRIT_KEY=registryId<Spirit.SpiritType<*>>("spirit")
    val SPIRIT=registry(SPIRIT_KEY)

    val SPIRIT_EXTRACTER_KEY=registryId<SpiritExtractor<*>>("spirit_extracter")
    val SPIRIT_EXTRACTER=registry(SPIRIT_EXTRACTER_KEY)


    val HEXVORTEX_HANDLER_SERIALIZER_KEY= registryId<HexVortexHandler.Serializer<*>>("hexvortex_handler_serializer")
    val HEXVORTEX_HANDLER_SERIALIZER= registry(HEXVORTEX_HANDLER_SERIALIZER_KEY)

    val HEXVORTEX_HANDLER_KEY= registryId<HexVortexHandler>("hexvortex_handler")
    val HEXVORTEX_HANDLER= dynamicRegistry(HEXVORTEX_HANDLER_KEY)


    val SPECIAL_SPIRIT_KEY= registryId<SpecialSpirit.SpecialType>("special_spirit")
    val SPECIAL_SPIRIT= dynamicRegistry(SPECIAL_SPIRIT_KEY)


    val RANK_KEY=registryId<LevelRanks.Rank>("mage_rank")
    val RANK= dynamicRegistry(RANK_KEY)

    private fun <T>registryId(id: String): RegistryKey<Registry<T>>{
        return RegistryKey.ofRegistry(Identifier(HexlinkMod.MODID,id))
    }

    @SuppressWarnings("unchecked-cast")
    private fun <T>registry(key: RegistryKey<Registry<T>>): SimpleRegistry<T>{
        val registry=SimpleRegistry(key, Lifecycle.stable(), null)
        val regRegistry=Registry.REGISTRIES as MutableRegistry<MutableRegistry<*>>
        Registry.register(regRegistry, key as RegistryKey<MutableRegistry<*>>, registry)
        return registry
    }

    private fun <T> dynamicRegistry(key: RegistryKey<Registry<T>>): SimpleRegistry<T> {
        return SimpleRegistry(key, Lifecycle.stable(), null)
    }

    fun <T>register(registry: MutableRegistry<T>, id: Identifier, value: T){
        val already_id= registry.getRawId(registry.get(id))
        if(already_id==-1){
            Registry.register(registry, id, value)
        }
        else{
            registry.replace(OptionalInt.empty(), RegistryKey.of(registry.key,id), value, Lifecycle.stable())
        }
    }
}