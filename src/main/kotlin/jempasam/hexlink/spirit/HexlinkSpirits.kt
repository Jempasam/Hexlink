package jempasam.hexlink.spirit

import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.HexlinkRegistry
import jempasam.hexlink.spirit.extracter.*
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry


@Suppress("unused")
object HexlinkSpirits {
    private fun <T :Spirit>create(id: String, spirit: Spirit.SpiritType<T>): Spirit.SpiritType<T>{
        Registry.register(HexlinkRegistry.SPIRIT, Identifier(HexlinkMod.MODID, id), spirit)
        return spirit
    }

    private fun <T: Spirit>create(id: String, spirit: SpiritExtractor<T>): SpiritExtractor<T>{
        Registry.register(HexlinkRegistry.SPIRIT_EXTRACTER, Identifier(HexlinkMod.MODID, id), spirit)
        return spirit
    }

    val ITEM_SPIRIT= create("item", ItemSpirit.Type)
    val ENTITY_SPIRIT= create("entity", EntitySpirit.Type)
    val POTION_SPIRIT= create("potion", PotionSpirit.Type)
    val BLOCK_SPIRIT= create("block", BlockSpirit.Type)
    val ENCHANTMENT_SPIRIT= create("enchantment", EnchantmentSpirit.Type)
    val BIOME_SPIRIT= create("biome", BiomeSpirit.Type)
    val COLOR_SPIRIT= create("color", ColorSpirit.Type)
    val FUNCTION_SPIRIT= create("function", FunctionSpirit.Type)
    val SPECIAL_SPIRIT= create("special", SpecialSpirit.Type)

    val ITEM_SPIRIT_EXTRACTOR= create("item", ItemExtractor)
    val ENTITY_SPIRIT_EXTRACTOR= create("entity", EntityExtractor)
    val POTION_SPIRIT_EXTRACTOR= create("potion", PotionExtractor)
    val BLOCK_SPIRIT_EXTRACTOR= create("block", BlockExtractor)
    val ENCHANTMENT_SPIRIT_EXTRACTOR= create("enchantment", EnchantmentExtractor)
    val BIOME_SPIRIT_EXTRACTOR= create("biome", BiomeExtractor)
    val EVERYTHING_SPIRIT_EXTRACTOR= create("everything", EverythingExtractor)
}