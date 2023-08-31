package jempasam.hexlink.utils

import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList

abstract class NbtAdapterList<T>(val nbt: NbtList): Collection<T>{
    protected abstract fun from(e: NbtElement): T?
    protected abstract fun to(e: T): NbtElement


    fun add(e: T) = nbt.add(to(e))

    fun add(index: Int, e: T) = nbt.add(index,to(e))
    operator fun set(i: Int, e: T) = nbt.set(i, to(e))
    fun remove(i: Int) = nbt.removeAt(i)


    operator fun get(i: Int): T?{
        val obj=from(nbt.get(i))
        if(obj==null){
            nbt.removeAt(i)
            return null
        }
        return obj
    }

    val first: T? get()=if(size>0) get(0) else null
    override fun isEmpty(): Boolean = nbt.isEmpty()
    override val size: Int get()= nbt.size
    override operator fun contains(element: T): Boolean = nbt.contains(to(element))
    override fun containsAll(elements: Collection<T>): Boolean = nbt.containsAll(elements.map { to(it) })
    override fun iterator(): Iterator<T> = nbt.asSequence().mapNotNull { from(it) }.iterator()
}