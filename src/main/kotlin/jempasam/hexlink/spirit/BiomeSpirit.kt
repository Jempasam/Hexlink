package jempasam.hexlink.spirit

import net.minecraft.block.Blocks
import net.minecraft.entity.Entity
import net.minecraft.entity.SpawnGroup
import net.minecraft.entity.SpawnReason
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.Util
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import net.minecraft.util.math.random.Random
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.util.registry.RegistryEntry
import net.minecraft.util.registry.RegistryEntryList
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.biome.Biome
import net.minecraft.world.gen.feature.FeaturePlacementContext
import net.minecraft.world.gen.feature.PlacedFeature
import java.util.*
import kotlin.jvm.optionals.getOrDefault


class BiomeSpirit(val biome: RegistryEntry<Biome>): Spirit{

    fun generateRandomFeature(world: ServerWorld, pos: BlockPos, features: RegistryEntryList<PlacedFeature>, rand: Random): Boolean{
        val feature=features.get(rand.nextInt(features.size())).value()
        val placements=feature.placementModifiers
        val configured=feature.feature.value()
        var final_pos=pos
        val context=FeaturePlacementContext(world, world.chunkManager.chunkGenerator, Optional.of(feature))
        for(placement in placements)final_pos=placement.getPositions(context, rand, final_pos).findFirst().getOrDefault(final_pos)

        val distance=pos.getSquaredDistance(final_pos)
        println("Distance is "+distance)
        if(distance<20*20){
            configured.generate(world, world.chunkManager.chunkGenerator, rand, pos)
            return true
        }
        else return false
    }

    fun generateFeatures(world: ServerWorld, position: BlockPos, number: Int, rand: Random){
        val groups=biome.value().generationSettings.features
        if(groups.size==0)return
        for(group in groups){
            if(group.size()==0)continue
            for(repetition in 0 until number){
                val offset_space=number-1
                val offset=Vec3i(
                        rand.nextBetween(-offset_space,offset_space+1),
                        0,
                        rand.nextBetween(-offset_space,offset_space+1)
                )
                val final_position=position.add(offset)
                var i=0
                while(!generateRandomFeature(world,final_position,group,rand) && i<10){
                    i++
                }
            }
        }
    }

    fun generateSnowAndIce(world: ServerWorld, position: BlockPos){
        if(biome.value().canSetSnow(world,position)) world.setBlockState(position,Blocks.SNOW.defaultState)
        if(biome.value().canSetIce(world,position)) world.setBlockState(position,Blocks.ICE.defaultState)
    }

    fun generateMob(world: ServerWorld, position: BlockPos, number: Int, rand: Random){
        val spawn_entries=biome.value().spawnSettings.getSpawnEntries(SpawnGroup.CREATURE).entries
        if(spawn_entries.size==0)return
        for(i in 0 until number){
            val spawn_entry=spawn_entries.get(rand.nextInt(spawn_entries.size))
            spawn_entry.type.spawn(world,null,null,null, position, SpawnReason.SPAWNER, false, false)
        }
    }

    fun generateAll(world: ServerWorld, position: BlockPos, number: Int){
        val rand=Random.create()
        generateFeatures(world,position,number,rand)
        generateMob(world,position,number,rand)
        generateSnowAndIce(world,position)
    }



    override fun infuseAtCost(caster: PlayerEntity, world: ServerWorld, position: Vec3d, power: Int): Int {
        return if(world.getBlockState(BlockPos(position)).isAir) 1 else Spirit.CANNOT_USE
    }

    override fun infuseAt(caster: PlayerEntity, world: ServerWorld, position: Vec3d, power: Int) {
        generateAll(world, BlockPos(position), power)
    }

    override fun infuseInCost(caster: PlayerEntity, world: ServerWorld, entity: Entity, power: Int): Int {
        return infuseAtCost(caster, world, entity.pos, power)
    }

    override fun infuseIn(caster: PlayerEntity, world: ServerWorld, entity: Entity, power: Int) {
        infuseAt(caster, world, entity.pos, power)
    }

    override fun lookAt(caster: PlayerEntity, world: ServerWorld, position: Vec3d): Boolean {
        return world.getBiome(BlockPos(position)).value()==biome.value()
    }

    override fun lookIn(caster: PlayerEntity, world: ServerWorld, entity: Entity): Boolean {
        return lookAt(caster, world, entity.pos)
    }

    override fun getColor(): Int = biome.value().foliageColor

    override fun getName(): Text
        = Text.translatable(Util.createTranslationKey("biome", biome.key.get().value))

    override fun equals(other: Any?): Boolean = other is BiomeSpirit && other.biome==biome


    override fun serialize(): NbtElement {
        return NbtString.of(biome.key.get().value.toString())
    }

    override fun getType(): Spirit.SpiritType<*> = Type

    object Type: Spirit.SpiritType<BiomeSpirit>{
        override fun getName(): Text = Text.translatable("hexlink.spirit.biome")

        override fun deserialize(nbt: NbtElement): BiomeSpirit? {
            if(nbt is NbtString){
                val biome=BuiltinRegistries.BIOME.getEntry(RegistryKey.of(BuiltinRegistries.BIOME.key,Identifier(nbt.asString())))
                if(biome.isEmpty)return null
                return BiomeSpirit(biome.get())
            }
            return null
        }
    }



}