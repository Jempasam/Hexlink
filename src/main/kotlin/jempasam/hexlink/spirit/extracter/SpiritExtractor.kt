package jempasam.hexlink.spirit.extracter

import jempasam.hexlink.spirit.Spirit
import net.minecraft.entity.Entity
import net.minecraft.text.Text

/**
 * Extract a Spirit Iota from a target entity
 */
interface SpiritExtractor<T: Spirit>{
    /**
     * Can extract from target entity
     * @return can extract from target
     */
    fun canExtract(target: Entity): Boolean

    /**
     * Extract from target entity
     * @return null if extraction fail because of unluck
     * @return the iota extracted
     */
    fun extract(target: Entity): ExtractionResult<T>

    /**
     * Consume the entity you have extracted from
     */
    fun consume(target: Entity)

    /**
     * Get a name describing extracted spirits
     */
    fun getExtractedName(): Text

    /**
     * Get a color representation of extracted spirits
     */
    fun getColor(): Int

    class ExtractionResult<T: Spirit>(val spirit: T, val count: Int)

    fun result(spirit: T, count: Int): ExtractionResult<T>{
        return ExtractionResult<T>(spirit,count)
    }


}