package jempasam.hexlink.particle


import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.particle.effect.MovingSpiritParticleEffect
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.minecraft.particle.DefaultParticleType
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleType
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import net.minecraft.registry.Registry
import net.minecraft.world.World

object HexlinkParticles {
    fun create(name: String): DefaultParticleType{
        val ret=FabricParticleTypes.simple()
        return create(name,ret)
    }

    fun <P: ParticleEffect, T: ParticleType<P>>create(name: String, type: T): T{
        Registry.register(Registries.PARTICLE_TYPE, Identifier(HexlinkMod.MODID,name), type)
        return type
    }

    val SPIRIT= create("spirit")
    val SPIRIT_FLUX= create("spirit_flux",MovingSpiritParticleEffect.Type)

    fun burst(world: World, pos: Vec3d, color: Int, count: Int){
        val r = (color shr 16 and 0xFF).toDouble() / 255.0
        val g = (color shr 8 and 0xFF).toDouble() / 255.0
        val b = (color shr 0 and 0xFF).toDouble() / 255.0
        for (j in 0 until count) {
            val pos=pos.add(Math.random(), Math.random(),Math.random())
            world.addParticle(
                    SPIRIT,
                    pos.x, pos.y, pos.z,
                    r, g, b
            )
        }
    }

    fun sendLink(world: ServerWorld, from: Vec3d, to: Vec3d, color: Int, count: Int){
        val offset=to.subtract(from).multiply(0.06)
        val r = (color shr 16 and 0xFF).toFloat() / 255.0f
        val g = (color shr 8 and 0xFF).toFloat() / 255.0f
        val b = (color shr 0 and 0xFF).toFloat() / 255.0f
        world.spawnParticles(
                MovingSpiritParticleEffect(r,g,b,count, offset),
                from.x, from.y, from.z,
                count/5,
                0.1,0.1,0.1,
                0.0
        )
    }
}