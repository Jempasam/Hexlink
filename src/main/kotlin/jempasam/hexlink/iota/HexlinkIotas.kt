package jempasam.hexlink.iota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.xplat.IXplatAbstractions
import jempasam.hexlink.HexlinkMod
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier


object HexlinkIotas{

    var IOTAS=HashMap<String,IotaType<*>>()

    fun <T: Iota>make(id: String, type: IotaType<T>): IotaType<T>{
        IOTAS.put(id, type)
        return type
    }

    fun registerAll(){
        for(entry in IOTAS) Registry.register(IXplatAbstractions.INSTANCE.iotaTypeRegistry, Identifier(HexlinkMod.MODID,entry.key), entry.value)
    }

    val SPIRIT= make("spirit", SpiritIota.Type)
}