package jempasam.hexlink.iota

import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.IotaType
import jempasam.hexlink.HexlinkRegistry
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.utils.NbtHelper
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class SpiritIota(spirit: Spirit) : Iota(Type, spirit) {
    fun getSpirit(): Spirit = payload as Spirit

    override fun toleratesOther(that: Iota): Boolean = that is SpiritIota && getSpirit()==that.getSpirit()

    override fun isTruthy(): Boolean = true

    override fun serialize(): NbtElement = NbtHelper.writeSpirit(getSpirit())

    object Type : IotaType<SpiritIota>(){
        override fun color(): Int = 0xCDE7FF

        override fun deserialize(tag: NbtElement, world: ServerWorld): SpiritIota? {
            if(tag is NbtCompound){
                val spirit= NbtHelper.readSpirit(tag) ?: return null
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

                val spirit_type= HexlinkRegistry.SPIRIT.get(Identifier(spirit_type_id)) ?: return Text.of("Invalid Block Spirit")

                val spirit=spirit_type.deserialize(spirit_value_nbt)
                return spirit?.getName()?.copy()?.append(Text.translatable("hexlink.spirit")) ?: throw Error("Should not happen")
            }
            return Text.of("Invalid Block Spirit")
        }
    }
}