package jempasam.hexlink.trinkets

import dev.emi.trinkets.api.Trinket
import dev.emi.trinkets.api.TrinketsApi
import jempasam.hexlink.item.HexlinkItems

object HexlinkTrinkets{
    fun registerTrinkets(){
        TrinketsApi.registerTrinket(HexlinkItems.FocusCollar, object: Trinket{})
    }
}