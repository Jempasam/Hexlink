package jempasam.hexlink.spirit.extractor

import jempasam.hexlink.spirit.Spirit
import net.minecraft.text.Text
import net.minecraft.util.math.ColorHelper
import net.minecraft.util.math.Vec3i

abstract class AbstractExtractor<T: Spirit>(val name: String, val colors: List<Vec3i>) : SpiritExtractor<T> {
    override fun getName(): Text {
        return Text.translatable(name)
    }

    override fun getColor(): Int {
        if(colors.size==1)return colors[0].run { ColorHelper.Argb.getArgb(255,x,y,z)  }
        val time=System.currentTimeMillis()%(1000*colors.size)/1000f
        val index=time.toInt()
        val localTime=((time-index)*1000).toInt()
        val nextIndex=(index+1)%2
        val color=(colors[index].multiply(localTime)).add(colors[nextIndex].multiply(1000-localTime))
        return ColorHelper.Argb.getArgb(255, color.x/1000, color.y/1000, color.z/1000)
    }
}