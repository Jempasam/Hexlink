package jempasam.hexlink.item.greatfocus

import at.petrak.hexcasting.api.spell.iota.Iota
import jempasam.hexlink.iota.spiritual.BlockSpiritIota
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.item.BlockItem

object BlockExtracter : GreatFocusExtracter<BlockSpiritIota> {
    override fun extract(target: Entity): Iota? {
        if(target is ItemEntity && target.stack.item is BlockItem){
            target.kill()
            val probability=successProbability(target)*target.stack.count
            if(Math.random()<probability)return BlockSpiritIota((target.stack.item as BlockItem).block)
            else return null
        }
        return null
    }

    override fun canExtract(target: Entity): Boolean {
        if(target is ItemEntity && target.stack.item is BlockItem){
            return true
        }
        return false
    }
}