package jempasam.hexlink.item.greatfocus

import at.petrak.hexcasting.api.spell.iota.Iota
import jempasam.hexlink.iota.spiritual.SpiritIota
import net.minecraft.entity.Entity

object EverythingExtracter : GreatFocusExtracter<SpiritIota> {
    override fun extract(target: Entity): Iota? {
        if(PotionExtracter.canExtract(target))return PotionExtracter.extract(target)
        if(BlockExtracter.canExtract(target))return BlockExtracter.extract(target)
        if(ItemExtracter.canExtract(target))return ItemExtracter.extract(target)
        if(EntityExtracter.canExtract(target))return EntityExtracter.extract(target)
        return null
    }

    override fun canExtract(target: Entity): Boolean {
        return  PotionExtracter.canExtract(target) ||
                BlockExtracter.canExtract(target) ||
                ItemExtracter.canExtract(target) ||
                EntityExtracter.canExtract(target)
    }
}