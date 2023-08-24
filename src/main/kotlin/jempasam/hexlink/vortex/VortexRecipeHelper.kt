package jempasam.hexlink.vortex

import jempasam.hexlink.HexlinkRegistry
import jempasam.hexlink.spirit.Spirit
import net.minecraft.server.world.ServerWorld

object VortexRecipeHelper {
    private val handler_map= mutableMapOf<Spirit,HexVortexHandler>()
    private val other_handkrs= mutableListOf<HexVortexHandler>()

    fun generateHandlerMaps(){
        handler_map.clear()
        other_handkrs.clear()
        for(handler in HexlinkRegistry.HEXVORTEX_HANDLER){
            if(handler is CatalyzedVortexHandler) handler_map[handler.getCatalyzer()] = handler
            else other_handkrs.add(handler)
        }
    }

    fun findRecipe(ingredients: List<Spirit>, world: ServerWorld): HexVortexHandler.Recipe?{
        if(ingredients.size>1){
            val first=ingredients.first()
            val catalyzed_handler= handler_map[first]
            if(catalyzed_handler!=null){
                val recipe=catalyzed_handler.findRecipe(ingredients, world)
                if(recipe!=null)return recipe
            }
            for(handler in other_handkrs){
                val recipe=handler.findRecipe(ingredients, world)
                if(recipe!=null)return recipe
            }
        }
        return null
    }
}