package jempasam.hexlink.particle.effect

import com.mojang.brigadier.StringReader
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.PacketByteBuf
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleType
import net.minecraft.util.math.Vec3d

class MovingSpiritParticleEffect(val red: Float, val green: Float, val blue: Float, val number: Int, val offset: Vec3d): ParticleEffect {
    override fun write(buf: PacketByteBuf) {
        buf.writeFloat(red)
        buf.writeFloat(green)
        buf.writeFloat(blue)
        buf.writeVarInt(number)
        buf.writeDouble(offset.x)
        buf.writeDouble(offset.y)
        buf.writeDouble(offset.z)
    }

    override fun getType(): ParticleType<*> = Type

    override fun asString(): String = "($red-$green-$blue)->$offset with $number"

    object Type: ParticleType<MovingSpiritParticleEffect>(false, Factory){
        override fun getCodec(): Codec<MovingSpiritParticleEffect> {
            return CODEC
        }
    }

    object Factory: ParticleEffect.Factory<MovingSpiritParticleEffect>{
        override fun read(type: ParticleType<MovingSpiritParticleEffect>, reader: StringReader): MovingSpiritParticleEffect {
            reader.expect(' ')
            return MovingSpiritParticleEffect(
                    reader.readFloat().also { reader.expect(' ') },
                    reader.readFloat().also { reader.expect(' ') },
                    reader.readFloat().also { reader.expect(' ') },
                    reader.readInt().also { reader.expect(' ') },
                    Vec3d(
                            reader.readDouble().also { reader.expect(' ') },
                            reader.readDouble().also { reader.expect(' ') },
                            reader.readDouble()
                    )
            )
        }

        override fun read(type: ParticleType<MovingSpiritParticleEffect>, buf: PacketByteBuf): MovingSpiritParticleEffect {
            return MovingSpiritParticleEffect(
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readVarInt(),
                    Vec3d(
                            buf.readDouble(),
                            buf.readDouble(),
                            buf.readDouble()
                    )
            )
        }

    }

    companion object{
        val CODEC= RecordCodecBuilder.create<MovingSpiritParticleEffect>{ builder ->
            builder.group(
                    Codec.FLOAT.fieldOf("red").forGetter{it.red},
                    Codec.FLOAT.fieldOf("green").forGetter{it.green},
                    Codec.FLOAT.fieldOf("blue").forGetter{it.blue},
                    Codec.INT.fieldOf("number").forGetter{it.number},
                    Vec3d.CODEC.fieldOf("offset").forGetter{it.offset}
            ).
            apply(builder, ::MovingSpiritParticleEffect)
        }
    }


}