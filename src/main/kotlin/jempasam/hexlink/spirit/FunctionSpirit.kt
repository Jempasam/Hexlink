package jempasam.hexlink.spirit

import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import java.util.*

class FunctionSpirit(val manifestAt: Identifier, val manifestIn: Identifier, val manifestAtCost: Int, val manifestInCost: Int) : Spirit{

    private val regex=Regex.fromLiteral("{_[a-z],[A-Z]}")
    private val name=manifestAt.path
            .substringAfter("/")
            .replace(regex){" "+it.value.uppercase()}

    override fun manifestAt(caster: PlayerEntity, world: ServerWorld, position: Vec3d, count: Int): Spirit.Manifestation {
        val function=world.server.commandFunctionManager.getFunction(manifestAt)
        if(function.isEmpty)
            return Spirit.NONE_MANIFESTATION
        else
            return Spirit.Manifestation(manifestAtCost, count){
                for(i in 0..<it){
                    val source=caster.commandSource.withPosition(position).withLevel(3)
                    world.server.commandFunctionManager.execute(function.get(),source)
                }
            }
    }

    override fun manifestIn(caster: PlayerEntity, world: ServerWorld, entity: Entity, count: Int): Spirit.Manifestation {
        val function=world.server.commandFunctionManager.getFunction(manifestIn)
        if(function.isEmpty)
            return Spirit.NONE_MANIFESTATION
        else
            return Spirit.Manifestation(manifestInCost, count){
                for(i in 0..<it){
                    val source=caster.commandSource.withEntity(entity).withPosition(entity.pos).withLevel(3)
                    world.server.commandFunctionManager.execute(function.get(),source)
                }
            }
    }

    override fun lookAt(caster: PlayerEntity, world: ServerWorld, position: Vec3d): Boolean = false

    override fun lookIn(caster: PlayerEntity, world: ServerWorld, entity: Entity): Boolean = false

    override fun getName(): Text = Text.of(name)

    override fun getColor(): Int = DyeColor.ORANGE.fireworkColor

    override fun equals(other: Any?): Boolean
        = other is FunctionSpirit &&
            other.manifestAt==manifestAt && other.manifestIn==manifestIn &&
            other.manifestAtCost==manifestAtCost && other.manifestInCost==manifestInCost

    override fun hashCode(): Int
        = Objects.hash(manifestIn, manifestAt, manifestInCost, manifestAtCost)

    override fun getType(): Spirit.SpiritType<*> = Type

    override fun serialize(): NbtCompound = NbtCompound().apply {
        putString("manifestAt", manifestAt.toString())
        putString("manifestIn", manifestIn.toString())
        putInt("manifestAtCost", manifestAtCost)
        putInt("manifestInCost", manifestInCost)
    }



    object Type : Spirit.SpiritType<FunctionSpirit>{
        override fun getName(): Text = Text.of("hexlink.spirit.function")

        override fun deserialize(nbt: NbtElement): FunctionSpirit? {
            if(nbt is NbtCompound){
                return FunctionSpirit(
                        Identifier(nbt.getString("manifestAt")),
                        Identifier(nbt.getString("manifestIn")),
                        nbt.getInt("manifestAtCost"),
                        nbt.getInt("manifestInCost")
                )
            }
            return null
        }
    }

}