import hexlink.iota.BlockTypeIota
import at.petrak.hexcasting.api.spell.iota.IotaType
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import net.minecraft.util.registry.Registry
import net.minecraft.util.Identifier
import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.iota.spiritual.BlockSpiritIota
import jempasam.hexlink.iota.spiritual.EntitySpiritIota
import jempasam.hexlink.iota.spiritual.ItemSpiritIota
import jempasam.hexlink.iota.spiritual.PotionSpiritIota


object HexlinkIotas{

    var IOTAS=HashMap<String,IotaType<*>>()

    fun <T: Iota>make(id: String, type: IotaType<T>): IotaType<T>{
        IOTAS.put(id, type)
        return type
    }

    fun registerAll(){
        for(entry in IOTAS) Registry.register(HexIotaTypes.REGISTRY, Identifier(HexlinkMod.MODID,entry.key), entry.value)
    }

    val BLOCK_TYPE= make("block_type", BlockTypeIota.TYPE);

    val SPIRIT_BLOCK= make("block_spirit", BlockSpiritIota.TYPE)
    val SPIRIT_ITEM= make("item_spirit", ItemSpiritIota.TYPE)
    val SPIRIT_POTION= make("potion_spirit", PotionSpiritIota.TYPE)
    val SPIRIT_ENTITY= make("entity_spirit", EntitySpiritIota.TYPE)
}