package jempasam.hexlink.spirit.extractor.special.node

import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.HexlinkRegistry
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object ExtNodeParsers {
    private fun <T: ExtractionNode.Parser<*>> register(id: String, node: T): T{
        Registry.register(HexlinkRegistry.EXTRACTOR_NODE_PARSER, Identifier(HexlinkMod.MODID,id), node)
        return node
    }

    val BIOME=register("biome",BiomeExtNode.Parser)
    val ENTITY=register("entity",EntityExtNode.Parser)
    val ITEM=register("item",ItemExtNode.Parser)
    val BLOCK=register("block",BlockExtNode.Parser)
    val POTION=register("potion",PotionExtNode.Parser)
    val ENCHANTMENT=register("enchantment",EnchantmentExtNode.Parser)

    val FILTER_ENTITY=register("entity_predicate",FilterEntityExtNode.Parser)
    val FILTER_ITEM=register("item_predicate",FilterItemExtNode.Parser)
    val ELSE=register("else",ElseExtNode.Parser)

    val RESULT=register("result",ResultNode.Parser)
}