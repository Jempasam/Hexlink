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
        var cur_count=count
        while(content.size>0 && cur_count>0){
            val last=content.first()
            cur_count-=last.count
            last.count-=cur_count
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
        var cur_count=count
        while(content.size>0 && cur_count>0){
            val last=content.last()
            cur_count-=last.count
            last.count-=cur_count
            if(last.count<=0)content.removeAt(content.size-1)
        }
    }

    fun last(): Spirit?= if(content.size>0) content.last().spirit else null

    fun lastStack(): Stack?= if(content.size>0) content.last() else null



    data class Stack(var spirit: Spirit, var count: Int)
}