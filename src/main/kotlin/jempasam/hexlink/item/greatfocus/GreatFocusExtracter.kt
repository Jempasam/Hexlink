package jempasam.hexlink.item.greatfocus

import at.petrak.hexcasting.api.spell.iota.Iota
import jempasam.hexlink.gamerule.HexlinkGamerules
import jempasam.hexlink.iota.spiritual.SpiritIota
import net.minecraft.entity.Entity

/**
 * Extract a Spirit Iota from an target entity
 */
interface GreatFocusExtracter<T: SpiritIota>{
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
    fun extract(target: Entity): Iota?

    fun successProbability(target: Entity): Double{
        return target.world.gameRules.get(HexlinkGamerules.EXTRACTION_PROBABILITY).get()
    }
}