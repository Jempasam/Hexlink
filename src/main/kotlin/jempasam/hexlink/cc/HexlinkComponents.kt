package jempasam.hexlink.cc

import dev.onyxstudios.cca.api.v3.level.LevelComponentFactoryRegistry
import dev.onyxstudios.cca.api.v3.level.LevelComponentInitializer

class HexlinkComponents : LevelComponentInitializer {
    override fun registerLevelComponentFactories(registry: LevelComponentFactoryRegistry) {
        registry.register(HexlinkLevelData.KEY,::HexlinkLevelData)
    }
}