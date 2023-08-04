package jempasam.hexlink.hud

import at.petrak.hexcasting.api.PatternRegistry
import at.petrak.hexcasting.api.spell.math.HexPattern
import com.mojang.blaze3d.systems.RenderSystem
import jempasam.hexlink.render.RenderHelper
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.decoration.ItemFrameEntity
import net.minecraft.item.ItemFrameItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Identifier
import net.minecraft.util.math.ColorHelper

class SpellHUD : HudRenderCallback{
    override fun onHudRender(matrixStack: MatrixStack, tickDelta: Float) {
        val mc=MinecraftClient.getInstance()
        var tessellator= Tessellator.getInstance()
        var buf= tessellator.buffer
        var matrix= matrixStack.peek().positionMatrix
        var normal= matrixStack.peek().normalMatrix


        DrawableHelper.drawCenteredText(matrixStack, mc.textRenderer,"Testuntest", 0, 0, ColorHelper.Argb.getArgb(255, 255, 0, 0))
        DrawableHelper.drawCenteredText(matrixStack, mc.textRenderer,"Testuntest", 50, 50, ColorHelper.Argb.getArgb(255, 255, 255, 0))


        fun drawPattern(color: Int, pattern: HexPattern){
            val a: ItemFrameEntity?=null
            with(RenderHelper){
                RenderHelper.color =ColorHelper.Argb.getArgb(100, 255, 255, 255)
                lineWidth=10
                renderPattern(matrixStack, pattern, 300f, 300f, 100F, 100F)

                RenderHelper.color =color
                lineWidth=5
                renderPattern(matrixStack, pattern, 300f, 300f, 100F, 100F)
            }
        }

        with(RenderHelper){
            color=ColorHelper.Argb.getArgb(255, 0, 0, 255)
            lineWidth=5
            renderLine(matrixStack, 10f, 10f, 100f, 100f)

            color=ColorHelper.Argb.getArgb(100, 255, 255, 0)
            lineWidth=10
            renderLine(matrixStack, 10f, 100f, 100f, 10f)

            color=ColorHelper.Argb.getArgb(255, 0, 255, 255)
            lineWidth=20
            renderLine(matrixStack, 100f, 10f, 100f, 100f)

            val pattern=PatternRegistry.lookupPattern(Identifier("hexcasting", "craft/artifact"))
            drawPattern(ColorHelper.Argb.getArgb(255,255,0,0), pattern.prototype)

            val pattern2=PatternRegistry.lookupPattern(Identifier("hexcasting", "craft/battery"))
            drawPattern(ColorHelper.Argb.getArgb(255,0,255,0), pattern2.prototype)

            val pattern3=PatternRegistry.lookupPattern(Identifier("hexcasting", "potion/weakness"))
            drawPattern(ColorHelper.Argb.getArgb(255,0,255,255), pattern3.prototype)
        }

        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.disableDepthTest()
        RenderSystem.disableCull()
        RenderSystem.disableTexture()
        RenderSystem.setShader(GameRenderer::getPositionColorShader)
        val color3=ColorHelper.Argb.getArgb(255, 0, 100, 255)
        buf.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)
        buf.vertex(matrix, 75f, 0f, 0f).color(color3).next()
        buf.vertex(matrix, 125f, 0f, 0f).color(color3).next()
        buf.vertex(matrix, 125f, 50f, 0f).color(color3).next()
        buf.vertex(matrix, 75f, 50f, 0f).color(color3).next()
        tessellator.draw()


        RenderSystem.enableDepthTest()
        RenderSystem.enableCull()
        RenderSystem.disableBlend()
        RenderSystem.enableTexture()

    }

}