package jempasam.hexlink.spirit.extracter

import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.spirit.Spirit
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text

/**
 * Extract a Spirit Iota from a target entity
 */
interface SpiritExtractor<T: Spirit>{


    /**
     * Try to extract spirits from an entity
     * @return The result containing the spirits extracted and that can consume the entity
     */
    fun extract(caster: PlayerEntity?, target: Entity): ExtractionResult<T>

    /**
     * Get a name describing extracted spirits
     */
    fun getName(): Text

    /**
     * Get a color representation of extracted spirits
     */
    fun getColor(): Int



    /**
     * The result of an extraction
     * @param spirit The spirit extracted
     * @param max_count The number of spirit extracted
     * @param consumer The method used to change or kill the entity after the spirit are extracted
     */
    class ExtractionResult<T: Spirit>(private val consumer: (Int)->Unit, val spirit: T?, val max_count: Int){
        /**
         * Consume the entity after extraction
         */
        fun consume(count: Int){
            if(count>max_count){
                HexlinkMod.logger.warn("Try to extract more than max")
                return consumer(max_count)
            }
            else return consumer(count)
        }

        fun downCast() = ExtractionResult<Spirit>(consumer,spirit,max_count)
    }

    fun noResult(): ExtractionResult<T> = noResult<T>()

    companion object{
        fun <T: Spirit>noResult(): ExtractionResult<T> = ExtractionResult({},null, 0)
    }

}