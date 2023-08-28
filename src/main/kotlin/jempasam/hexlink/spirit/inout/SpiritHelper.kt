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
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
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

    fun spiritTarget(caster: PlayerEntity?, stack: ItemStack): SpiritTarget{
        val item=stack.item
        return when{
            item is ItemSpiritTarget -> item.getSpiritTarget(stack)
            else -> SpiritTarget.NONE
        }
    }

    fun spiritTarget(caster: PlayerEntity): SpiritTarget{
        val inventory=caster.inventory
        return object: SpiritTarget{
            override fun fill(count: Int, spirit: Spirit): SpiritTarget.SpiritInputFlux {
                val main_flux= spiritTarget(caster,caster.mainHandStack).fill(count, spirit)
                if(main_flux.maxcount>0)return main_flux

                val offh_flux= spiritTarget(caster,caster.offHandStack).fill(count, spirit)
                if(offh_flux.maxcount>0)return offh_flux

                for(i in 0 ..< inventory.size()){
                    val stack= inventory.getStack(i)
                    val flux= spiritTarget(caster,stack).fill(count, spirit)
                    if(flux.maxcount>0)return flux
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

    fun spiritSource(caster: PlayerEntity?, stack: ItemStack): SpiritSource{
        val item=stack.item
        return when{
            item is ItemSpiritSource -> item.getSpiritSource(stack)
            else -> SpiritSource.NONE
        }
    }

    fun spiritSource(caster: PlayerEntity): SpiritSource{
        val inventory=caster.inventory
        return object: SpiritSource{
            override fun extract(count: Int, spirit: Spirit): SpiritSource.SpiritOutputFlux {
                val main_flux= spiritSource(caster,caster.mainHandStack).extract(count,spirit)
                if(main_flux.maxcount>0)return main_flux

                val off_flux= spiritSource(caster,caster.offHandStack).extract(count,spirit)
                if(off_flux.maxcount>0)return off_flux

                if(caster.isCreative)return SpiritSource.SpiritOutputFlux({},count)
                for(i in 0 ..< inventory.size()){
                    val stack=inventory.getStack(i)
                    val flux= spiritSource(caster,stack).extract(count,spirit)
                    if(flux.maxcount>0)return flux
                }
                return SpiritSource.NONE.FLUX
            }

            override fun last(): Spirit?{
                val main_flux= spiritSource(caster,caster.mainHandStack).last()
                if(main_flux!=null)return main_flux

                val off_flux= spiritSource(caster,caster.offHandStack).last()
                if(off_flux!=null)return off_flux

                for(i in 0 ..< inventory.size()){
                    val stack=inventory.getStack(i)
                    val flux= spiritSource(caster,stack).last()
                    if(flux!=null)return flux
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

    fun asSpirit(item: Item): Spirit{
        if(item is BlockItem)return BlockSpirit(item.block)
        else return ItemSpirit(item)
    }
}