package jempasam.hexlink.item

import at.petrak.hexcasting.api.item.ColorizerItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.math.ColorHelper
import net.minecraft.util.math.Vec3d
import java.util.*

/**
 * Customizable pigment with bugged transition
 */
class MixedPigmentItem(settings: Item.Settings) : Item(settings), ColorizerItem {
    override fun color(stack: ItemStack, owner: UUID, time: Float, position: Vec3d): Int {
        val roading=Math.sin(Math.PI*time)
        val color1=getColor1(stack)
        val color2=getColor2(stack)
        val r=( ColorHelper.Argb.getRed(color1)*roading + ColorHelper.Argb.getRed(color2)*(1-roading) ).toInt()
        val g=( ColorHelper.Argb.getGreen(color1)*roading + ColorHelper.Argb.getGreen(color2)*(1-roading) ).toInt()
        val b=( ColorHelper.Argb.getBlue(color1)*roading + ColorHelper.Argb.getBlue(color2)*(1-roading) ).toInt()
        return ColorHelper.Argb.getArgb(255, r, g, b)
    }

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