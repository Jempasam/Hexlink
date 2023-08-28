package jempasam.hexlink.spirit

import jempasam.hexlink.HexlinkRegistry
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import kotlin.jvm.optionals.getOrNull

class SpecialSpirit(val special_type: SpecialType) : Spirit {
    override fun getColor(): Int = special_type.color

    override fun getName(): Text = special_type.text

    override fun equals(other: Any?): Boolean = other is SpecialSpirit && special_type===other.special_type

    override fun hashCode(): Int = special_type.hashCode()

    override fun lookAt(caster: PlayerEntity, world: ServerWorld, position: Vec3d): Boolean
        = special_type.lookAt.lookAt(caster, world, position)

    override fun lookIn(caster: PlayerEntity, world: ServerWorld, entity: Entity): Boolean
        = special_type.lookIn.lookIn(caster, world, entity)

    override fun manifestAt(caster: PlayerEntity, world: ServerWorld, position: Vec3d, count: Int): Spirit.Manifestation
        = special_type.manifestAt.manifestAt(caster, world, position, count)

    override fun manifestIn(caster: PlayerEntity, world: ServerWorld, entity: Entity, count: Int): Spirit.Manifestation
        = special_type.manifestIn.manifestIn(caster, world, entity, count)

    override fun serialize(): NbtElement {
        return NbtString.of(HexlinkRegistry.SPECIAL_SPIRIT.getKey(special_type).getOrNull()?.value?.toString() ?: "")
    }

    override fun getType(): Spirit.SpiritType<*> = Type

    object Type: Spirit.SpiritType<SpecialSpirit>{
        override fun getName(): Text
            = Text.of("hexlink.spirit.special")

        override fun deserialize(nbt: NbtElement): SpecialSpirit?
            = HexlinkRegistry.SPECIAL_SPIRIT.get(Identifier(nbt.asString()))?.let { SpecialSpirit(it) }
    }

    class SpecialType(val manifestAt: Spirit, val manifestIn: Spirit, val lookAt: Spirit, val lookIn: Spirit, val text: Text, val color: Int)
}