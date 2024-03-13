package jempasam.hexlink.item

import net.minecraft.item.Item
import net.minecraft.item.ItemStack

/**
 * Customizable pigment with bugged transition
 */
class MixedPigmentItem(settings: Settings) : Item(settings) {
    /*TODO override fun color(stack: ItemStack, owner: UUID, time: Float, position: Vec3d): Int {
        val roading= sin(Math.PI*(time/2)%Math.PI)
        val unroading=1f-roading
        val color1=getColor1(stack)
        val color2=getColor2(stack)
        val r=( (color1%0x1000000/0X10000)*roading + (color2%0x1000000/0X10000)*unroading ).toInt()
        val g=( (color1%0x10000/0x100)*roading + (color2%0x10000/0x100)*unroading ).toInt()
        val b=( (color1%0x100)*roading + (color2%0x100)*unroading ).toInt()
        return r*0x10000+g*0x100+b
    }*/

    fun getColor1(stack: ItemStack): Int{
        return stack.orCreateNbt.getInt("color1")
    }

    fun getColor2(stack: ItemStack): Int{
        return stack.orCreateNbt.getInt("color2")
    }

    fun setColor1(stack: ItemStack, color1: Int){
        stack.orCreateNbt.putInt("color1", color1)
    }

    fun setColor2(stack: ItemStack, color2: Int){
        stack.orCreateNbt.putInt("color2", color2)
    }

    override fun getDefaultStack(): ItemStack {
        val ret=super.getDefaultStack()
        setColor1(ret,0xFFFFFF)
        setColor2(ret, 0xFFFFFF)
        return ret
    }
}