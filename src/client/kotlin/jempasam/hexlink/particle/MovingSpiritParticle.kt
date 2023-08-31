package jempasam.hexlink.particle

import jempasam.hexlink.particle.effect.MovingSpiritParticleEffect
import net.minecraft.client.particle.*
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.math.MathHelper
import kotlin.math.max

class MovingSpiritParticle(world: ClientWorld, x: Double, y: Double, z: Double, velocityX: Double, velocityY: Double, velocityZ: Double, val spriteProvider: SpriteProvider, number: Int)
    : SpriteBillboardParticle(world, x, y, z, velocityX, velocityY, velocityZ)
{
    val prob= max(if(number<=0) maxAge/2 else maxAge/(number+1),1)
    init {
        setSpriteForAge(spriteProvider)
        gravityStrength=0.0f
        this.velocityX=velocityX
        this.velocityY=velocityY
        this.velocityZ=velocityZ
    }
    override fun getType(): ParticleTextureSheet = ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;

    override fun tick() {
        super.tick()
        if(age%prob==0)world.addParticle(HexlinkParticles.SPIRIT,x,y,z,red.toDouble(),green.toDouble(),blue.toDouble())
        setSpriteForAge(this.spriteProvider)
        setAlpha(MathHelper.lerp(0.05f as Float, alpha as Float, 1.0f as Float))
    }


    class Factory(private val spriteProvider: SpriteProvider) : ParticleFactory<MovingSpiritParticleEffect> {
        override fun createParticle(type: MovingSpiritParticleEffect, clientWorld: ClientWorld, d: Double, e: Double, f: Double, g: Double, h: Double, i: Double): Particle {
            val spellParticle = MovingSpiritParticle(
                    clientWorld,
                    d, e, f,
                    type.offset.x, type.offset.y, type.offset.z,
                    spriteProvider, type.number
            )
            spellParticle.setColor(type.red, type.green, type.blue)
            return spellParticle
        }
    }

}