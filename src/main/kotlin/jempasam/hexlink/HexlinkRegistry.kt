package jempasam.hexlink

import com.mojang.serialization.Lifecycle
import jempasam.hexlink.recipe.vortex.HexVortexHandler
import jempasam.hexlink.spirit.SpecialSpirit
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.extractor.SpiritExtractor
import jempasam.hexlink.spirit.extractor.node.ExtractionNode
import jempasam.hexlink.world.LevelRanks
import net.minecraft.registry.*
import net.minecraft.util.Identifier

object HexlinkRegistry {
    // Spirit and extractors
    val SPIRIT_KEY=registryId<Spirit.SpiritType<*>>("spirit")
    val SPIRIT=registry(SPIRIT_KEY)

    val EXTRACTOR_KEY=registryId<SpiritExtractor<*>>("spirit_extractor")
    val EXTRACTOR=dynamicRegistry(EXTRACTOR_KEY)

    val EXTRACTOR_SERIALIZER_KEY=registryId<SpiritExtractor.Serializer<*>>("extractor_serializer")
    val EXTRACTOR_SERIALIZER=registry(EXTRACTOR_KEY)

    val EXTRACTOR_NODE_PARSER_KEY=registryId<ExtractionNode.Parser<*>>("extractor_parser")
    val EXTRACTOR_NODE_PARSER=registry(EXTRACTOR_NODE_PARSER_KEY)


    val HEXVORTEX_HANDLER_PARSER_KEY= registryId<HexVortexHandler.Parser<*>>("hexvortex_handler_parser")
    val HEXVORTEX_HANDLER_PARSER= registry(HEXVORTEX_HANDLER_PARSER_KEY)

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
        val registry=SimpleRegistry(key, Lifecycle.stable(), false)
        val regRegistry=Registries.REGISTRIES as MutableRegistry<MutableRegistry<*>>
        Registry.register(regRegistry, key as RegistryKey<MutableRegistry<*>>, registry)
        return registry
    }

    private fun <T> dynamicRegistry(key: RegistryKey<Registry<T>>): SimpleRegistry<T> {
        return SimpleRegistry(key, Lifecycle.stable(), false)
    }

    fun <T>register(registry: MutableRegistry<T>, id: Identifier, value: T){
        val already_id= registry.getRawId(registry.get(id))
        if(already_id==-1){
            Registry.register(registry, id, value)
        }
        else{
            //registry.replace(OptionalInt.empty(), RegistryKey.of(registry.key,id), value, Lifecycle.stable())
        }
    }
}