package jempasam.hexlink.render;

import at.petrak.hexcasting.api.spell.math.HexPattern
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.*
import kotlin.math.max

object RenderHelper {

    var lineWidth: Int=4
    var color: Int=ColorHelper.Argb.getArgb(255, 0, 0, 255)

    fun renderLine(stack: MatrixStack, x1: Float, y1: Float, x2: Float, y2: Float){
        val direction=Vec2f(x2-x1, y2-y1).normalize().multiply(lineWidth/2.0f)
        val sidex=direction.y
        val sidey=-direction.x

        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.disableDepthTest()
        RenderSystem.disableCull()
        RenderSystem.disableTexture()
        RenderSystem.setShader(GameRenderer::getPositionColorShader)

        val matrix=stack.peek().positionMatrix
        val buf=Tessellator.getInstance().buffer
        buf.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)
        buf.vertex(matrix, x1-sidex, y1-sidey, 0f).color(color).next()
        buf.vertex(matrix, x1+sidex, y1+sidey, 0f).color(color).next()
        buf.vertex(matrix, x2+sidex, y2+sidey, 0f).color(color).next()
        buf.vertex(matrix, x2-sidex, y2-sidey, 0f).color(color).next()
        Tessellator.getInstance().draw()

        RenderSystem.enableDepthTest()
        RenderSystem.enableCull()
        RenderSystem.disableBlend()
        RenderSystem.enableTexture()
    }

    fun renderPattern(stack: MatrixStack, pattern: HexPattern, centerx: Float, centery: Float, maxwidth: Float, maxheight: Float){
        val (size,shapecenter)= getSizeAndCenter(pattern)
        val size_ratio=Math.min(maxwidth/size.x, maxheight/size.y)
        val angle_step=(Math.PI/3f).toFloat()
        val direction=Vec3f.NEGATIVE_Y.copy()
        val move=Vec3f()
        direction.rotate(Quaternion.fromEulerXyz(0f, 0f, (pattern.startDir.ordinal)*angle_step+angle_step/2))

        val last_dest=Vec3f(
                centerx - size_ratio*size.x + shapecenter.x*size_ratio,
                centery - size_ratio*size.y + shapecenter.y*size_ratio,
                0.0f
        )
        val dest=last_dest.copy().apply { move.set(direction); move.multiplyComponentwise(size_ratio,size_ratio,size_ratio); add(move) }

        renderLine(stack, last_dest.x, last_dest.y, dest.x, dest.y)
        for(line in pattern.angles){
            last_dest.set(dest)
            direction.rotate(Quaternion.fromEulerXyz(0f, 0f, line.ordinal*angle_step))
            move.run { set(direction); multiplyComponentwise(size_ratio,size_ratio,size_ratio) }
            dest.add(move)
            renderLine(stack, last_dest.x, last_dest.y, dest.x, dest.y)
        }
    }

    fun getSizeAndCenter(pattern: HexPattern): Pair<Vec2f,Vec2f>{
        val angle_step=(Math.PI/3f).toFloat()
        val direction=Vec3f.NEGATIVE_Y.copy()
        direction.rotate(Quaternion.fromEulerXyz(0f, 0f, (pattern.startDir.ordinal)*angle_step+angle_step/2))

        val last_dest=Vec3f.ZERO.copy()
        val dest=last_dest.copy().apply { add(direction) }

        var minx=last_dest.x;
        var miny=last_dest.y;
        var maxx=last_dest.x;
        var maxy=last_dest.y;

        for(line in pattern.angles){
            last_dest.set(dest)
            direction.rotate(Quaternion.fromEulerXyz(0f, 0f, line.ordinal*angle_step))
            dest.add(direction)
            if(dest.x>maxx)maxx=dest.x
            if(dest.x<minx)minx=dest.x
            if(dest.y>maxy)maxy=dest.y
            if(dest.y<miny)miny=dest.y
        }
        return Vec2f(maxx-minx, maxy-miny) to Vec2f(0-minx, 0-miny)
    }
}
