package jempasam.hexlink.spirit

import jempasam.hexlink.HexlinkMod
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
import net.minecraft.world.gen.feature.ConfiguredFeature
import net.minecraft.world.gen.feature.FeaturePlacementContext
import net.minecraft.world.gen.feature.PlacedFeature
import net.minecraft.world.gen.placementmodifier.PlacementModifierType
import java.util.*


class BiomeSpirit(val biome: RegistryEntry<Biome>): Spirit{

    companion object{
        const val VALIDATION_SQUARE_DISTANCE=400.0f // Distance of 20
        const val MAX_TRY_COUNT=5
    }

    fun placeAll(world: ServerWorld, center: BlockPos, positions: List<BlockPos>, feature: ConfiguredFeature<*,*>, rand: Random): Int{
        if(positions.isEmpty())return 0

        var spawn_count=0
        var minx=Int.MAX_VALUE
        var miny=Int.MAX_VALUE
        var minz=Int.MAX_VALUE
        var maxx=Int.MIN_VALUE
        var maxy=Int.MIN_VALUE
        var maxz=Int.MIN_VALUE
        for(pos in positions){
            if(pos.x<minx)minx=pos.x
            if(pos.y<miny)miny=pos.y
            if(pos.z<minz)minz=pos.z
            if(pos.x>maxx)maxx=pos.x
            if(pos.y>maxy)maxy=pos.y
            if(pos.z>maxz)maxz=pos.z
        }

        val offset=Vec3i(
                center.x-(minx+(maxx-minx)/2),
                center.y-(miny+(maxy-miny)/2),
                center.z-(minz+(maxz-minz)/2)
        )

        for(spawn_pos in positions){
            val square_distance=spawn_pos.getSquaredDistance(center)
            if(square_distance< VALIDATION_SQUARE_DISTANCE){
                if(feature.generate(world, world.chunkManager.chunkGenerator, rand, spawn_pos.add(offset)))
                    spawn_count++
            }
        }

        return spawn_count
    }

    fun generateFeature(world: ServerWorld, start_pos: BlockPos, feature: PlacedFeature, rand: Random): Boolean{
        val placements=feature.placementModifiers
        val configured=feature.feature.value()

        // Get structure positions
        var positions= listOf(start_pos)
        val context=FeaturePlacementContext(world, world.chunkManager.chunkGenerator, Optional.of(feature))
        for(placement in placements){
            val newpositions=mutableListOf<BlockPos>()
            if(placement.type!= PlacementModifierType.BIOME){
                for(pos in positions){
                    for(newpos in placement.getPositions(context, rand, pos)){
                        newpositions.add(newpos)
                    }
                }
                positions=newpositions
                if(positions.isEmpty())break
            }
        }

        // Try Spawning Features
        val spawn_count=placeAll(world, start_pos, positions, configured, rand)
        return spawn_count>1
    }

    fun generateRandomFeature(world: ServerWorld, pos: BlockPos, features: RegistryEntryList<PlacedFeature>, rand: Random): Boolean{
        var try_count=0
        var selected=rand.nextInt(features.size())
        while(try_count< MAX_TRY_COUNT){
            val feature=features.get(selected%features.size())
            HexlinkMod.logger.info("Try "+feature.key.get())
            if(generateFeature(world, pos, feature.value(), rand)){
                HexlinkMod.logger.info("Success with "+feature.key.get())
                return true
            }
            selected++
            try_count++
        }
        return false
    }

    fun generateFeatures(world: ServerWorld, position: BlockPos, number: Int, rand: Random){
        val groups=biome.value().generationSettings.features
        if(groups.size==0)return
        var seq=0
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
                if(!generateRandomFeature(world,final_position,group,rand))
                    HexlinkMod.logger.info("Biome Spirit generation structure n$repetition of step $seq fail")
                seq++
            }
        }
    }

    fun generateSnowAndIce(world: ServerWorld, position: BlockPos){
        val finalpos=position.up()
        if(biome.value().canSetSnow(world,finalpos)) world.setBlockState(finalpos,Blocks.SNOW.defaultState)
        if(biome.value().canSetIce(world,finalpos)) world.setBlockState(finalpos,Blocks.ICE.defaultState)
    }

    fun generateMob(world: ServerWorld, position: BlockPos, number: Int, rand: Random){
        val finalpos=position.up()
        val spawn_entries=biome.value().spawnSettings.getSpawnEntries(SpawnGroup.CREATURE).entries
        if(spawn_entries.size==0)return
        for(i in 0 until number){
            val spawn_entry= spawn_entries[rand.nextInt(spawn_entries.size)]
            spawn_entry.type.spawn(world,null,null,null, finalpos, SpawnReason.SPAWNER, false, false)
        }
    }

    fun generateAll(world: ServerWorld, position: BlockPos, number: Int){
        val rand=Random.create()
        generateFeatures(world,position,number,rand)
        generateMob(world,position,number,rand)
        generateSnowAndIce(world,position)
    }



    override fun infuseAtCost(caster: PlayerEntity, world: ServerWorld, position: Vec3d, power: Int): Int {
        return if(world.getBlockState(BlockPos(position).up()).isAir) 1 else Spirit.CANNOT_USE
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

    override fun hashCode(): Int = biome.hashCode()


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