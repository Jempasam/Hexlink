package jempasam.hexlink.spirit

import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.DyeItem
import net.minecraft.item.DyeableItem
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtInt
import net.minecraft.particle.DustParticleEffect
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.math.ColorHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3f

class ColorSpirit(private val color: Int) : Spirit {
    private val entry= find(color)

    override fun getColor(): Int = color

    override fun getName(): Text = Text.of(entry.name)

    override fun equals(other: Any?): Boolean = other is ColorSpirit && color==other.color

    override fun hashCode(): Int = color*873

    fun colorize(stack: StackHelper.WorldStack?): Spirit.Manifestation{
        if(stack==null)return Spirit.NONE_MANIFESTATION
        val item=stack.stack.item
        if(item is DyeableItem){
            return Spirit.Manifestation(5,1){
                item.setColor(stack.stack,color)
                stack.update()
            }
        }
        return Spirit.NONE_MANIFESTATION
    }

    override fun manifestAt(caster: PlayerEntity, world: ServerWorld, position: Vec3d, count: Int): Spirit.Manifestation{
        return Spirit.Manifestation(1,count){
            val color=Vec3f(
                    ColorHelper.Argb.getRed(color)/255f,
                    ColorHelper.Argb.getGreen(color)/255f,
                    ColorHelper.Argb.getBlue(color)/255f
            )
            world.spawnParticles(
                    DustParticleEffect(color, 1.0f*count),
                    position.x, position.y, position.z,
                    count,
                    0.1*count, 0.1*count, 0.1*count,
                    0.1+0.01*count
            )
        }
    }

    override fun manifestIn(caster: PlayerEntity, world: ServerWorld, entity: Entity, count: Int): Spirit.Manifestation
        = colorize(StackHelper.stack(caster,entity))


    fun testColor(stack: StackHelper.WorldStack?): Boolean{
        if(stack==null)return false
        val item=stack.stack.item
        if(item is DyeableItem){
            return color==item.getColor(stack.stack)
        }
        return false
    }
    override fun lookAt(caster: PlayerEntity, world: ServerWorld, position: Vec3d): Boolean
            = testColor(StackHelper.stack(caster,world,position))

    override fun lookIn(caster: PlayerEntity, world: ServerWorld, entity: Entity): Boolean
            = testColor(StackHelper.stack(caster,entity))



    override fun serialize(): NbtElement {
        return NbtInt.of(color)
    }

    override fun getType(): Spirit.SpiritType<*> = Type

    object Type: Spirit.SpiritType<ColorSpirit>{
        override fun getName(): Text = Text.translatable("hexlink.spirit.color")

        override fun deserialize(nbt: NbtElement): ColorSpirit?
            = (nbt as? NbtInt)?.let { ColorSpirit(it.intValue()) }
    }


    companion object{
        private data class ColorCode(val r: Int, val g: Int, val b:Int)
        private data class ColorEntry(val dye: DyeItem, val name: String, val hash: Int)
        private fun color(r: Int, g: Int, b: Int, item: Item, name: String): Pair<ColorCode,ColorEntry>
            = ColorCode(r,g,b) to ColorEntry(item as DyeItem, name, r*100+g*10+b)

        private fun find(color: Int): ColorEntry = color_map[ColorCode(
                ColorHelper.Argb.getRed(color)/86,
                ColorHelper.Argb.getGreen(color)/86,
                ColorHelper.Argb.getBlue(color)/86
            )] ?: invalid_color

        private val invalid_color=ColorEntry(Items.BLACK_DYE as DyeItem, "Invalid Color", 9999)
        private val color_map= mapOf(
            color(0,0,0, Items.BLACK_DYE, "Black"),
            color(0,0,1, Items.BLUE_DYE, "Dark Blue"),
            color(0,0,2, Items.BLUE_DYE, "Blue"),
            color(0,1,0, Items.GREEN_DYE, "Dark green"),
            color(0,1,1, Items.CYAN_DYE, "Dark Cyan"),
            color(0,1,2, Items.LIGHT_BLUE_DYE, "Sky Blue"),
            color(0,2,0, Items.LIME_DYE, "Green"),
            color(0,2,1, Items.LIME_DYE, "Mint"),
            color(0,2,2, Items.CYAN_DYE, "Cyan"),
            color(1,0,0, Items.RED_DYE, "Dark Red"),
            color(1,0,1, Items.PURPLE_DYE, "Purple"),
            color(1,0,2, Items.PURPLE_DYE, "Lavender"),
            color(1,1,0, Items.GREEN_DYE, "Kaki"),
            color(1,1,1, Items.BLACK_DYE, "Black"),
            color(1,1,2, Items.GRAY_DYE, "Black"),
            color(1,2,0, Items.LIME_DYE, "Lime"),
            color(1,2,1, Items.LIME_DYE, "Pale Lime"),
            color(1,2,2, Items.LIGHT_BLUE_DYE, "Light Blue"),
            color(2,0,0, Items.RED_DYE, "Red"),
            color(2,0,1, Items.PINK_DYE, "Fuschia"),
            color(2,0,2, Items.MAGENTA_DYE, "Magenta"),
            color(2,1,0, Items.ORANGE_DYE, "Orange"),
            color(2,1,1, Items.PINK_DYE, "Tulip"),
            color(2,1,2, Items.PINK_DYE, "Fuschia Pink"),
            color(2,2,0, Items.YELLOW_DYE, "Yellow"),
            color(2,2,1, Items.YELLOW_DYE, "Light Yellow"),
            color(2,2,2, Items.WHITE_DYE, "White")
        )
    }
}