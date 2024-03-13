package jempasam.hexlink.spirit

import jempasam.hexlink.HexlinkMod
import net.fabricmc.fabric.api.event.registry.DynamicRegistries
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
import net.minecraft.registry.BuiltinRegistries
import net.minecraft.registry.RegistryEntry
import net.minecraft.registry.RegistryEntryList
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.registry.entry.RegistryEntryList
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

        var spawnCount=0
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

        for(spawnPos in positions){
            val squareDistance=spawnPos.getSquaredDistance(center)
            if(squareDistance< VALIDATION_SQUARE_DISTANCE){
                if(feature.generate(world, world.chunkManager.chunkGenerator, rand, spawnPos.add(offset)))
                    spawnCount++
            }
        }

        return spawnCount
    }

    fun generateFeature(world: ServerWorld, startPos: BlockPos, feature: PlacedFeature, rand: Random): Boolean{
        val placements=feature.placementModifiers
        val configured=feature.feature.value()

        // Get structure positions
        var positions= listOf(startPos)
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
        val spawnCount=placeAll(world, startPos, positions, configured, rand)
        return spawnCount>1
    }

    fun generateRandomFeature(world: ServerWorld, pos: BlockPos, features: RegistryEntryList<PlacedFeature>, rand: Random): Boolean{
        var tryCount=0
        var selected=rand.nextInt(features.size())
        while(tryCount< MAX_TRY_COUNT){
            val feature=features.get(selected%features.size())
            HexlinkMod.logger.info("Try "+feature.key.get())
            if(generateFeature(world, pos, feature.value(), rand)){
                HexlinkMod.logger.info("Success with "+feature.key.get())
                return true
            }
            selected++
            tryCount++
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
                val offsetSpace=number-1
                val offset=Vec3i(
                        rand.nextBetween(-offsetSpace,offsetSpace+1),
                        0,
                        rand.nextBetween(-offsetSpace,offsetSpace+1)
                )
                val finalPosition=position.add(offset)
                if(!generateRandomFeature(world,finalPosition,group,rand))
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
        val spawnEntries=biome.value().spawnSettings.getSpawnEntries(SpawnGroup.CREATURE).entries
        if(spawnEntries.size==0)return
        for(i in 0 until number){
            val spawnEntry= spawnEntries[rand.nextInt(spawnEntries.size)]
            spawnEntry.type.spawn(world,null,null,null, finalpos, SpawnReason.SPAWNER, false, false)
        }
    }

    fun generateAll(world: ServerWorld, position: BlockPos, number: Int){
        val rand=Random.create()
        generateFeatures(world,position,number,rand)
        generateMob(world,position,number,rand)
        generateSnowAndIce(world,position)
    }



    override fun manifestAt(caster: PlayerEntity, world: ServerWorld, position: Vec3d, count: Int): Spirit.Manifestation {
        if(!world.getBlockState(BlockPos.ofFloored(position).up()).isAir)
            return Spirit.NONE_MANIFESTATION
        else
            return Spirit.Manifestation(1,count){
                generateAll(world, BlockPos.ofFloored(position), it)
            }
    }

    override fun manifestIn(caster: PlayerEntity, world: ServerWorld, entity: Entity, count: Int): Spirit.Manifestation {
        return manifestAt(caster,world,entity.pos.add(0.0,-0.5,0.0),count)
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