package jempasam.hexlink.item.greatfocus

import at.petrak.hexcasting.api.spell.iota.Iota
import jempasam.hexlink.iota.spiritual.ItemSpiritIota
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity

object ItemExtracter : GreatFocusExtracter<ItemSpiritIota> {

    override fun extract(target: Entity): Iota? {
        if(target is ItemEntity){
            target.kill()
            val probability=successProbability(target)*target.stack.count
            if(Math.random()<probability)return ItemSpiritIota(target.stack.item)
            else return null
        }
        return null
    }

    override fun canExtract(target: Entity): Boolean {
        if(target is ItemEntity){
            return true
        }
        return false
    }
}