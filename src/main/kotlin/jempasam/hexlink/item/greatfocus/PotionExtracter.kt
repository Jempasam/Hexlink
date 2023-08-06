package jempasam.hexlink.item.greatfocus

import at.petrak.hexcasting.api.spell.iota.Iota
import jempasam.hexlink.iota.spiritual.PotionSpiritIota
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.item.Items
import net.minecraft.potion.PotionUtil

object PotionExtracter : GreatFocusExtracter<PotionSpiritIota> {
    override fun extract(target: Entity): Iota? {
        if(target is ItemEntity && target.stack.item== Items.POTION){
            val effects= PotionUtil.getPotionEffects(target.stack)
            if(!effects.isEmpty()){
                target.kill()
                val probability=successProbability(target)*target.stack.count
                if(Math.random()<probability) return PotionSpiritIota(effects.get(0).effectType)
                else return null
            }
        }
        return null
    }

    override fun canExtract(target: Entity): Boolean {
        if(target is ItemEntity && target.stack.item== Items.POTION){
            val effects= PotionUtil.getPotionEffects(target.stack)
            if(!effects.isEmpty())return true
        }
        return false
    }
}