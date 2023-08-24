package jempasam.hexlink.vortex

import jempasam.hexlink.HexlinkRegistry
import jempasam.hexlink.spirit.Spirit
import net.minecraft.server.world.ServerWorld

object VortexRecipeHelper {
    private val root=NodeMap()

    private class NodeMap{

        val map= mutableMapOf<Spirit,NodeMap>()
        val remaining= mutableListOf<HexVortexHandler>()
        fun find(ingredients: List<Spirit>, start: Int, world: ServerWorld): HexVortexHandler.Recipe?{
            if(start<ingredients.size){
                val finded=map.get(ingredients.get(start))
                if(finded!=null)finded.find(ingredients, start+1, world)
            }

            for(h in remaining){
                val ret=h.findRecipe(ingredients.subList(start,ingredients.size), world)
                if(ret!=null)return ret
            }
            return null
        }

        fun add(catalyzer: List<Spirit>, start: Int, handler: HexVortexHandler){
            if(start<catalyzer.size){
                var next_node=map.get(catalyzer[start])
                if(next_node==null){
                    next_node= NodeMap()
                    map.put(catalyzer[start],next_node)
                }
                next_node.add(catalyzer, start+1, handler)
            }
            else remaining.add(handler)
        }
    }

    fun generateHandlerMaps(){
        root.map.clear()
        root.remaining.clear()
        for(handler in HexlinkRegistry.HEXVORTEX_HANDLER){
            if(handler is CatalyzedVortexHandler){
                root.add(handler.getCatalyzer(), 0, handler)
            }
            else root.remaining.add(handler)
        }
    }

    fun findRecipe(ingredients: List<Spirit>, world: ServerWorld): HexVortexHandler.Recipe?
        = root.find(ingredients, 0, world)
}