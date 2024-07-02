package jempasam.hexlink

import com.mojang.serialization.Lifecycle
import jempasam.hexlink.recipe.vortex.HexVortexHandler
import jempasam.hexlink.spirit.SpecialSpirit
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.extractor.NodeExtractor
import jempasam.hexlink.spirit.extractor.SpiritExtractor
import jempasam.hexlink.spirit.extractor.node.ExtractionNode
import jempasam.hexlink.utils.LoadableRegistry
import jempasam.hexlink.world.LevelRanks
import net.minecraft.util.Identifier
import net.minecraft.util.registry.MutableRegistry
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.util.registry.SimpleRegistry
import java.util.*

object HexlinkRegistry {
    // Spirit and extractors
    val SPIRIT_KEY=registryId<Spirit.SpiritType<*>>("spirit")
    val SPIRIT=registry(SPIRIT_KEY)

    val EXTRACTOR_KEY=registryId<NodeExtractor>("spirit_extractor")
    val EXTRACTOR=loadableRegistry(EXTRACTOR_KEY)

    val EXTRACTOR_SERIALIZER_KEY=registryId<SpiritExtractor.Serializer<*>>("extractor_serializer")
    val EXTRACTOR_SERIALIZER=registry(EXTRACTOR_KEY)

    val EXTRACTOR_NODE_PARSER_KEY=registryId<ExtractionNode.Parser<*>>("extractor_parser")
    val EXTRACTOR_NODE_PARSER=registry(EXTRACTOR_NODE_PARSER_KEY)


    val HEXVORTEX_HANDLER_PARSER_KEY= registryId<HexVortexHandler.Parser<*>>("hexvortex_handler_parser")
    val HEXVORTEX_HANDLER_PARSER= registry(HEXVORTEX_HANDLER_PARSER_KEY)

    val HEXVORTEX_HANDLER_KEY= registryId<HexVortexHandler>("hexvortex_handler")
    var HEXVORTEX_HANDLER= loadableRegistry(HEXVORTEX_HANDLER_KEY)

    val SPECIAL_SPIRIT_KEY= registryId<SpecialSpirit.SpecialType>("special_spirit")
    var SPECIAL_SPIRIT= loadableRegistry(SPECIAL_SPIRIT_KEY)


    val RANK_KEY=registryId<LevelRanks.Rank>("mage_rank")
    val RANK= loadableRegistry(RANK_KEY)

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

    private fun <T> loadableRegistry(key: RegistryKey<Registry<T>>): LoadableRegistry<T> {
        return LoadableRegistry(key, Lifecycle.stable())
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