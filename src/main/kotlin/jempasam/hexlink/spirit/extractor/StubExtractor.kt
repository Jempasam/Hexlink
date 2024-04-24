package jempasam.hexlink.spirit.extractor

import jempasam.hexlink.spirit.Spirit
import net.minecraft.entity.Entity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

/**
 * Extract a Spirit Iota from a target entity
 */
class StubExtractor(private val name: Text, private val color: Int, private val cost: Int): SpiritExtractor<Spirit>{
    override fun extract(caster: ServerPlayerEntity?, target: Entity) = SpiritExtractor.ExtractionResult(null as Spirit?, 0) {}

    override fun getName() = name

    override fun getColor() = color

    override fun getCost() = cost

}