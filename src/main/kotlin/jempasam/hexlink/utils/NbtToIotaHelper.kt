package jempasam.hexlink.utils

import at.petrak.hexcasting.api.spell.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.spell.iota.ListIota
import at.petrak.hexcasting.api.spell.iota.NullIota
import net.minecraft.nbt.*
import ram.talia.moreiotas.api.spell.iota.StringIota

object NbtToIotaHelper {
    fun NbtCompound.toIota(): ListIota {
        val ret= mutableListOf<Iota>()
        for(key in this.keys){
            ret.add(ListIota(listOf(StringIota(key),this.get(key)?.toIota())))
        }
        return ListIota(ret)
    }

    fun AbstractNbtList<*>.toIota(): ListIota {
        val ret= mutableListOf<Iota>()
        for(e in this)ret.add(e.toIota())
        return ListIota(ret)
    }

    fun NbtString.toIota(): StringIota = StringIota(this.asString())

    fun NbtByte.toIota(): DoubleIota = DoubleIota(this.doubleValue())
    fun NbtShort.toIota(): DoubleIota = DoubleIota(this.doubleValue())
    fun NbtInt.toIota(): DoubleIota = DoubleIota(this.doubleValue())
    fun NbtLong.toIota(): DoubleIota = DoubleIota(this.doubleValue())

    fun NbtFloat.toIota(): DoubleIota = DoubleIota(this.doubleValue())
    fun NbtDouble.toIota(): DoubleIota = DoubleIota(this.doubleValue())

    fun NbtElement.toIota(): Iota {
        return when(this){
            is NbtCompound -> this.toIota()
            is AbstractNbtList<*> -> this.toIota()

            is NbtString -> this.toIota()

            is NbtByte -> this.toIota()
            is NbtShort -> this.toIota()
            is NbtInt -> this.toIota()
            is NbtLong -> this.toIota()

            is NbtFloat -> this.toIota()
            is NbtDouble -> this.toIota()

            else -> NullIota()
        }
    }
}