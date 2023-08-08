package jempasam.hexlink.iota

import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.IotaType
import jempasam.hexlink.HexlinkRegistry
import jempasam.hexlink.spirit.Spirit
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class SpiritIota(spirit: Spirit) : Iota(Type, spirit) {
    fun getSpirit(): Spirit = payload as Spirit

    override fun toleratesOther(that: Iota): Boolean = that is SpiritIota && getSpirit().equals(that.getSpirit())

    override fun isTruthy(): Boolean = true

    override fun serialize(): NbtElement = getSpirit().serialize()

    object Type : IotaType<SpiritIota>(){
        override fun color(): Int = 0xF1F400

        override fun deserialize(tag: NbtElement, world: ServerWorld): SpiritIota {
            if(tag is NbtCompound){
                val spirit_type_id=tag.getString("type")
                val spirit_value_nbt=tag.get("value")
                if(spirit_type_id=="")throw IllegalArgumentException()
                if(spirit_value_nbt==null)throw IllegalArgumentException()

                val spirit_type=HexlinkRegistry.SPIRIT.get(Identifier(spirit_type_id))
                if(spirit_type==null)throw IllegalArgumentException()

                val spirit=spirit_type.deserialize(spirit_value_nbt)
                return SpiritIota(spirit)
            }
            throw IllegalArgumentException()
        }

        override fun display(tag: NbtElement): Text {
            if(tag is NbtCompound){
                val spirit_type_id=tag.getString("type")
                val spirit_value_nbt=tag.get("value")
                if(spirit_type_id=="")return Text.of("Invalid Block Spirit")
                if(spirit_value_nbt==null)return Text.of("Invalid Block Spirit")

                val spirit_type=HexlinkRegistry.SPIRIT.get(Identifier(spirit_type_id))
                if(spirit_type==null)return Text.of("Invalid Block Spirit")

                val spirit=spirit_type.deserialize(spirit_value_nbt)
                return spirit.getName().copy().append("hexlink.spirit")
            }
            return Text.of("Invalid Block Spirit")
        }
    }
}