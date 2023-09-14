package jempasam.hexlink.iota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.IotaType
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import jempasam.hexlink.HexlinkMod
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry


object HexlinkIotas{

    var IOTAS=HashMap<String,IotaType<*>>()

    fun <T: Iota>make(id: String, type: IotaType<T>): IotaType<T>{
        IOTAS.put(id, type)
        return type
    }

    fun registerAll(){
        for(entry in IOTAS) Registry.register(HexIotaTypes.REGISTRY, Identifier(HexlinkMod.MODID,entry.key), entry.value)
    }

    val SPIRIT= make("spirit", SpiritIota.Type)
}