package jempasam.hexlink.particle


import jempasam.hexlink.HexlinkMod
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.minecraft.particle.DefaultParticleType
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import kotlin.math.min

object HexlinkParticles {
    fun create(name: String): DefaultParticleType{
        val ret=FabricParticleTypes.simple()
        Registry.register(Registry.PARTICLE_TYPE, Identifier(HexlinkMod.MODID,name), ret)
        return ret
    }

    val SPIRIT= create("spirit")

    fun burst(world: World, pos: Vec3d, color: Int, count: Int){
        val r = (color shr 16 and 0xFF).toDouble() / 255.0
        val g = (color shr 8 and 0xFF).toDouble() / 255.0
        val b = (color shr 0 and 0xFF).toDouble() / 255.0
        for (j in 0 until count) {
            val pos=pos.add(Math.random(), Math.random(),Math.random())
            world.addParticle(
                    HexlinkParticles.SPIRIT,
                    pos.x, pos.y, pos.z,
                    r, g, b
            )
        }
    }

    fun line(world: World, from: Vec3d, to: Vec3d, color: Int){
        val r = (color shr 16 and 0xFF).toDouble() / 255.0
        val g = (color shr 8 and 0xFF).toDouble() / 255.0
        val b = (color shr 0 and 0xFF).toDouble() / 255.0
        val count= min(to.distanceTo(from)*2,20.0)
        val vec=to.subtract(from).multiply(1.0/count)
        var pt=from
        for (j in 0..<count.toInt()) {
            pt=pt.add(vec)
            world.addParticle(
                    SPIRIT,
                    pt.x, pt.y, pt.z,
                    r, g, b
            )
        }
    }
}