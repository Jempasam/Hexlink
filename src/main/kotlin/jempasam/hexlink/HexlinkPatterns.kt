package jempasam.hexlink

import at.petrak.hexcasting.api.PatternRegistry
import at.petrak.hexcasting.api.spell.Action
import at.petrak.hexcasting.api.spell.math.HexDir
import at.petrak.hexcasting.api.spell.math.HexPattern
import jempasam.hexlink.operators.OpSpiritTest
import jempasam.hexlink.operators.rw.OpReadTrinket
import jempasam.hexlink.operators.rw.OpWriteTrinket
import jempasam.hexlink.operators.spells.*
import net.minecraft.util.Identifier

object HexlinkPatterns {

    private fun register(id: String, pattern: HexPattern, op : Action){
        PatternRegistry.mapPattern(pattern, Identifier(HexlinkMod.MODID,id), op)
    }

    fun registerAll(){
        register("spirit_extract",
                HexPattern.fromAngles("aawddaweddwaa",HexDir.NORTH_EAST),
                OpExtractSpirit()
        )

        register("spirit_extract_entity",
                HexPattern.fromAngles("aawddaweddwwawaw",HexDir.NORTH_EAST),
                OpEntityExtractSpirit()
        )

        register("spirit_manifestation",
                HexPattern.fromAngles("aawddawedd",HexDir.NORTH_EAST),
                OpManisfestSpirit()
        )

        register("spirit_look",
                HexPattern.fromAngles("aawddawqaa",HexDir.NORTH_EAST),
                OpSpiritTest()
        )

        register("spirit_mix",
                HexPattern.fromAngles("aawddawqdd",HexDir.NORTH_EAST),
                OpMixSpirit()
        )

        register("spirit_transfer",
                HexPattern.fromAngles("aawddaweaqa",HexDir.NORTH_EAST),
                OpSpiritTransfer()
        )

        register("read_trinket",
                HexPattern.fromAngles("aqwqqqwq",HexDir.EAST),
                OpReadTrinket()
        )

        register("write_trinket",
                HexPattern.fromAngles("deweeewe",HexDir.EAST),
                OpWriteTrinket()
        )
    }
}