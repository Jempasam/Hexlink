package jempasam.hexlink.spirit.inout

import jempasam.hexlink.block.functionnality.BlockSpiritSource
import jempasam.hexlink.block.functionnality.BlockSpiritTarget
import jempasam.hexlink.item.functionnality.ItemSpiritSource
import jempasam.hexlink.item.functionnality.ItemSpiritTarget
import jempasam.hexlink.spirit.BlockSpirit
import jempasam.hexlink.spirit.ItemSpirit
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.StackHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

object SpiritHelper{

    fun transfer(source: SpiritSource, target: SpiritTarget, spirit: Spirit, max_count: Int): Pair<()->Unit,Int>{
        val source_flux=source.extract(max_count, spirit)
        val target_flux=target.fill(source_flux.maxcount, spirit)

        return {
            source_flux.consume(target_flux.maxcount)
            target_flux.fill(target_flux.maxcount)
        } to target_flux.maxcount
    }

    fun spiritTarget(caster: PlayerEntity): SpiritTarget{
        val inventory=caster.inventory
        return object: SpiritTarget{
            override fun fill(count: Int, spirit: Spirit): SpiritTarget.SpiritInputFlux {
                for(i in 0 ..< inventory.size()){
                    val stack=inventory.getStack(i)
                    val item=stack.item
                    if(item is ItemSpiritTarget){
                        val spirit_flux=item.getSpiritTarget(stack).fill(count, spirit)
                        if(spirit_flux.maxcount>0)return SpiritTarget.SpiritInputFlux({spirit_flux.fill(it)},spirit_flux.maxcount)
                    }
                }
                return SpiritTarget.NONE.FLUX
            }
        }

    }

    fun spiritTarget(caster: PlayerEntity?, world: ServerWorld, pos: Vec3d): SpiritTarget?{
        val bpos=BlockPos(pos)
        val state=world.getBlockState(bpos)
        val blocktype=state.block
        if(blocktype is BlockSpiritTarget){
            return blocktype.getSpiritTarget(world, bpos)
        }

        val world_stack=StackHelper.stack(caster, world, pos)
        if(world_stack!=null){
            val item=world_stack.stack.item
            if(item is ItemSpiritTarget)return StackUpdateSpiritTarget(world_stack, item.getSpiritTarget(world_stack.stack))
        }

        return null
    }

    fun spiritTarget(caster: PlayerEntity?, entity: Entity): SpiritTarget?{
        if(entity==caster){
            return spiritTarget(caster)
        }

        if(entity is SpiritTarget)return entity

        val world_stack=StackHelper.stack(caster, entity)
        if(world_stack!=null){
            val item=world_stack.stack.item
            if(item is ItemSpiritTarget)return StackUpdateSpiritTarget(world_stack, item.getSpiritTarget(world_stack.stack))
        }

        return null
    }



    fun spiritSource(caster: PlayerEntity): SpiritSource{
        val inventory=caster.inventory
        return object: SpiritSource{
            override fun extract(count: Int, spirit: Spirit): SpiritSource.SpiritOutputFlux {
                for(i in 0 ..< inventory.size()){
                    val stack=inventory.getStack(i)
                    val item=stack.item
                    if(item is ItemSpiritSource){
                        val spirit_flux=item.getSpiritSource(stack).extract(count, spirit)
                        if(spirit_flux.maxcount!=0)return SpiritSource.SpiritOutputFlux({spirit_flux.consume(it)},spirit_flux.maxcount)
                    }
                }
                return SpiritSource.NONE.FLUX
            }

            override fun last(): Spirit?{
                for(i in 0 ..< inventory.size()){
                    val stack=inventory.getStack(i)
                    val item=stack.item
                    if(item is ItemSpiritSource)return item.getSpiritSource(stack).last()
                }
                return null
            }
        }

    }

    fun spiritSource(caster: PlayerEntity?, world: ServerWorld, pos: Vec3d): SpiritSource?{
        val bpos=BlockPos(pos)
        val state=world.getBlockState(bpos)
        val blocktype=state.block
        if(blocktype is BlockSpiritSource)return blocktype.getSpiritSource(world, bpos)

        val world_stack=StackHelper.stack(caster, world, pos)
        if(world_stack!=null){
            val item=world_stack.stack.item
            if(item is ItemSpiritSource)return StackUpdateSpiritSource(world_stack, item.getSpiritSource(world_stack.stack))
        }
        return null
    }

    fun spiritSource(caster: PlayerEntity?, entity: Entity): SpiritSource?{
        if(entity==caster){
            return spiritSource(caster)
        }

        if(entity is SpiritSource)return entity

        val world_stack=StackHelper.stack(caster, entity)
        if(world_stack!=null){
            val item=world_stack.stack.item
            if(item is ItemSpiritSource)return StackUpdateSpiritSource(world_stack, item.getSpiritSource(world_stack.stack))
        }

        return null
    }



    private class StackUpdateSpiritSource(val stack: StackHelper.WorldStack, val source: SpiritSource): SpiritSource{
        override fun extract(count: Int, spirit: Spirit): SpiritSource.SpiritOutputFlux {
            val source=source.extract(count,spirit)
            if(source.maxcount>0){
                return SpiritSource.SpiritOutputFlux({source.consume(it); stack.update()}, source.maxcount)
            }
            else return SpiritSource.NONE.FLUX
        }

        override fun last(): Spirit? = source.last()
    }

    private class StackUpdateSpiritTarget(val stack: StackHelper.WorldStack, val target: SpiritTarget): SpiritTarget{
        override fun fill(count: Int, spirit: Spirit): SpiritTarget.SpiritInputFlux {
            val flux=target.fill(count,spirit)
            if(flux.maxcount>0){
                return SpiritTarget.SpiritInputFlux({flux.fill(it); stack.update()}, flux.maxcount)
            }
            else return SpiritTarget.NONE.FLUX
        }
    }

    fun asItem(spirit: Spirit): Item?{
        if(spirit is ItemSpirit)return spirit.item
        else if(spirit is BlockSpirit && spirit.block.asItem()!= Items.AIR)return spirit.block.asItem()
        else return null
    }
}