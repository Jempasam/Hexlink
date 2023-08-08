package jempasam.hexlink

import com.mojang.serialization.Lifecycle
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.extracter.SpiritExtractor
import net.minecraft.util.Identifier
import net.minecraft.util.registry.RegistryKey
import net.minecraft.util.registry.SimpleRegistry

object HexlinkRegistry {
    val SPIRIT_KEY=RegistryKey.ofRegistry<Spirit.SpiritType<*>>(Identifier(HexlinkMod.MODID,"spirit"))
    val SPIRIT=SimpleRegistry(SPIRIT_KEY, Lifecycle.stable(), null)

    val SPIRIT_EXTRACTER_KEY=RegistryKey.ofRegistry<SpiritExtractor<*>>(Identifier(HexlinkMod.MODID,"spirit_extracter"))
    val SPIRIT_EXTRACTER=SimpleRegistry(SPIRIT_EXTRACTER_KEY, Lifecycle.stable(), null)
}