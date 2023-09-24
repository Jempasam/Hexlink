package jempasam.hexlink.spirit.bag

import at.petrak.hexcasting.api.utils.putCompound
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.utils.NbtHelper
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class SpiritBag : Collection<Spirit>{
    private val content: MutableList<Stack>
    private var totalCount: Int

    constructor(){
        content= mutableListOf<Stack>()
        totalCount= 0
    }
    constructor(copied: SpiritBag){
        content=copied.content.asSequence().map { it.copy() }.toMutableList()
        totalCount=copied.totalCount
    }

    fun clear() = content.clear()

    fun pushFront(spirit: Spirit, count: Int){
        if(content.size>0 && content.first().spirit==spirit){
            content.first().count+=count
        }
        else content.add(0, Stack(spirit,count))
        totalCount+=count
    }

    fun popFront(count: Int){
        var curCount=count
        while(content.size>0 && curCount>0){
            val last=content.first()
            val removed=min(last.count,curCount)
            curCount-=removed
            last.count-=removed
            if(last.count<=0){
                content.removeAt(0)
                totalCount-=removed
            }
        }
    }

    fun pushBack(spirit: Spirit, count: Int){
        if(content.size>0 && content.last().spirit==spirit){
            content.last().count+=count
        }
        else content.add(Stack(spirit,count))
        totalCount+=count
    }

    fun pushBack(bag: SpiritBag){
        for(stack in bag.content){
            pushBack(stack.spirit,stack.count)
        }
    }

    fun popBack(count: Int){
        var curCount=count
        while(content.size>0 && curCount>0){
            val last=content.last()
            val removed=min(last.count,curCount)
            last.count-=removed
            curCount-=removed
            if(last.count<=0){
                content.removeAt(content.size-1)
                totalCount-=removed
            }
        }
    }

    fun subBag(size: Int): SpiritBag{
        var count=size
        val ret=SpiritBag()
        for(e in content){
            val added=min(count,e.count)
            ret.pushBack(e.spirit,added)
            count-=added
            if(count==0)break
        }
        return ret
    }

    operator fun get(index: Int) = content[index]

    fun count(spirit: Spirit): Int = content.asSequence().filter { it.spirit==spirit }.sumOf { it.count }

    fun remove(spirit: Spirit, count: Int){
        var toremove=count
        val it=content.asReversed().listIterator()
        while(it.hasNext()){
            val stack=it.next()
            if(stack.spirit==spirit){
                val removed= min(stack.count,toremove)
                stack.count-=removed
                if(stack.count==0)it.remove()
                toremove-=removed
                totalCount-=removed
                if(toremove==0)break
            }
        }
    }

    fun last(): Spirit?= if(content.size>0) content.last().spirit else null

    fun lastStack(): Stack?= if(content.size>0) content.last() else null

    val stackCount get()=content.size

    override val size get()=totalCount

    override fun isEmpty(): Boolean = content.isEmpty()

    override fun contains(element: Spirit): Boolean = content.any { it.spirit==element }

    override fun containsAll(elements: Collection<Spirit>): Boolean = elements.all { contains(it) }

    override fun iterator(): Iterator<Spirit> = object : Iterator<Spirit>{
        var stacki=0
        var locali=0
        override fun hasNext(): Boolean {
            return stacki<content.size && locali<content[stacki].count
        }

        override fun next(): Spirit {
            if(stacki>=content.size)throw NoSuchElementException()
            val ret=content[stacki].spirit
            locali++
            if(locali>=content[stacki].count){
                stacki++
                locali=0
            }
            return ret
        }
    }

    fun reverseIterator(): Iterator<Spirit> = object : Iterator<Spirit>{
        var stacki=content.size-1
        var locali= if(content.isEmpty()) 0 else content[stacki].count-1

        override fun hasNext(): Boolean {
            return stacki>0 || locali>0
        }

        override fun next(): Spirit {
            if(stacki<0)throw NoSuchElementException()
            val ret=content[stacki].spirit
            locali--
            if(locali<0){
                stacki--
                locali=content.size-1
            }
            return ret
        }
    }

    fun random(): Spirit = content[Random.nextInt(content.size)].spirit

    fun readNBT(nbt: NbtList){
        content.clear()
        totalCount=0;
        nbt.forEach { cpd ->
            if(cpd is NbtCompound){
                val count= max(cpd.getInt("count"),1)
                val spirit= NbtHelper.readSpirit(cpd.getCompound("spirit"))
                if(spirit!=null){
                    content.add(Stack(spirit,count))
                    totalCount+=count
                }
            }
        }
    }

    fun writeNBT(): NbtList{
        return NbtList().apply {
            content.forEach { stack ->
                add(NbtCompound().apply {
                    putInt("count",stack.count)
                    putCompound("spirit", NbtHelper.writeSpirit(stack.spirit))
                })
            }
        }
    }



    data class Stack(var spirit: Spirit, var count: Int)
}