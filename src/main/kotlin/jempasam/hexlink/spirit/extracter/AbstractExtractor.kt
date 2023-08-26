package jempasam.hexlink.spirit.extracter

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
        val local_time=((time-index)*1000).toInt()
        val next_index=(index+1)%2
        val color=(colors[index].multiply(local_time)).add(colors[next_index].multiply(1000-local_time))
        return ColorHelper.Argb.getArgb(255, color.x/1000, color.y/1000, color.z/1000)
    }
}