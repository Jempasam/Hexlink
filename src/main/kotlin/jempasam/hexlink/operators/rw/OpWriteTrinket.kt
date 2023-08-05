package jempasam.hexlink.operators.rw

import at.petrak.hexcasting.api.item.IotaHolderItem
import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import dev.emi.trinkets.api.TrinketsApi
import jempasam.hexlink.item.HexlinkItems
import jempasam.hexlink.mishap.MishapNoCollarItem

class OpWriteTrinket : ConstMediaAction {
    override val argc: Int
        get() = 1

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val towrite=args[0]
        TrinketsApi.getTrinketComponent(ctx.caster).ifPresent{
            val slot=it.getEquipped(HexlinkItems.FocusCollar).firstOrNull { it.right.item is IotaHolderItem }
            if(slot==null)throw MishapNoCollarItem()
            val holder=slot.right
            val item=holder.item
            if(item is IotaHolderItem){
                item.writeDatum(holder,towrite)
            }
        }
        return listOf()
    }
}