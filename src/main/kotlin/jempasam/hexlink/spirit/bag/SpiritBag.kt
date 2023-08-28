package jempasam.hexlink.spirit.bag

import jempasam.hexlink.spirit.Spirit

class SpiritBag {
    private val content= mutableListOf<Stack>()

    fun clear() = content.clear()

    fun pushFront(spirit: Spirit, count: Int){
        if(content.size>0 && content.first().spirit==spirit){
            content.first().count+=count
        }
        else content.add(0, Stack(spirit,count))
    }

    fun popFront(count: Int){
        var curCount=count
        while(content.size>0 && curCount>0){
            val last=content.first()
            curCount-=last.count
            last.count-=curCount
            if(last.count<=0)content.removeAt(0)
        }
    }

    fun pushBack(spirit: Spirit, count: Int){
        if(content.size>0 && content.last().spirit==spirit){
            content.last().count+=count
        }
        else content.add(Stack(spirit,count))
    }

    fun popBack(count: Int){
        var curCount=count
        while(content.size>0 && curCount>0){
            val last=content.last()
            curCount-=last.count
            last.count-=curCount
            if(last.count<=0)content.removeAt(content.size-1)
        }
    }

    operator fun get(index: Int) = content[index]

    fun last(): Spirit?= if(content.size>0) content.last().spirit else null

    fun lastStack(): Stack?= if(content.size>0) content.last() else null

    val stackCount get()=content.size



    data class Stack(var spirit: Spirit, var count: Int)
}