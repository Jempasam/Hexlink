package jempasam.hexlink.item

import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.inout.SpiritSource
import net.minecraft.item.ItemStack
import net.minecraft.util.math.random.Random

class RandomSpiritItem(settings: Settings): InfiniteSpiritItem(settings) {

    override fun getSpiritSource(stack: ItemStack): SpiritSource {
        return object: SpiritSource {
            override fun last(): Spirit? = getSpirits(stack).run { if(size>0) this[kotlin.random.Random.nextInt(size)] else null }
            override fun extract(count: Int, spirit: Spirit): SpiritSource.SpiritOutputFlux {
                val fcount= if(isDamageable) stack.maxDamage-stack.damage else count
                if(getSpirits(stack).contains(spirit))return SpiritSource.SpiritOutputFlux(fcount) {
                    if(stack.isDamageable)stack.damage(fcount, Random.create(),null)
                }
                else return SpiritSource.NONE.FLUX
            }
        }
    }

}