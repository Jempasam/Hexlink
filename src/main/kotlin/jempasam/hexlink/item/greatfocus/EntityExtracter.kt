package jempasam.hexlink.item.greatfocus

import at.petrak.hexcasting.api.spell.iota.Iota
import jempasam.hexlink.iota.spiritual.EntitySpiritIota
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity

object EntityExtracter : GreatFocusExtracter<EntitySpiritIota> {
    override fun extract(target: Entity): Iota? {
        if(target.type.isSummonable && (target !is LivingEntity || target.health<3.0f)){
            target.kill()
            val probability=BlockExtracter.successProbability(target)*2
            if(Math.random()<probability)return EntitySpiritIota(target.type)
            else return null
        }
        return null
    }

    override fun canExtract(target: Entity): Boolean {
        if(target.type.isSummonable && (target !is LivingEntity || target.health<3.0f)){
            return true
        }
        return false
    }
}