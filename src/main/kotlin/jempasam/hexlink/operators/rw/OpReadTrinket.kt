package jempasam.hexlink.operators.rw

import at.petrak.hexcasting.api.item.IotaHolderItem
import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.NullIota
import dev.emi.trinkets.api.TrinketsApi
import jempasam.hexlink.item.HexlinkItems
import jempasam.hexlink.mishap.MishapEmptyCollarItem
import jempasam.hexlink.mishap.MishapNoCollarItem

class OpReadTrinket : ConstMediaAction{
    override val argc: Int
        get() = 0

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        var readed: Iota?=null
        TrinketsApi.getTrinketComponent(ctx.caster).ifPresent{
            val slot= it.getEquipped(HexlinkItems.FocusCollar).firstOrNull{ it.right.item is IotaHolderItem }
                    ?: throw MishapNoCollarItem()
            val holder=slot.right
            val item=holder.item
            if(item is IotaHolderItem)readed=item.readIota(holder,ctx.world)
            if(readed==null)throw MishapEmptyCollarItem(holder)
        }
        return listOf(readed?:NullIota())
    }
}