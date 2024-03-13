package jempasam.hexlink

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.PatternRegistry
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.Action
import at.petrak.hexcasting.api.spell.math.HexDir
import at.petrak.hexcasting.api.spell.math.HexPattern
import at.petrak.hexcasting.common.casting.PatternRegistryManifest
import jempasam.hexlink.operators.OpSpiritTest
import jempasam.hexlink.operators.OpStub
import jempasam.hexlink.operators.spells.*
import jempasam.hexlink.operators.spiritinfo.OpGetSpiritIota
import jempasam.hexlink.operators.spiritinfo.OpSpiritCountIota
import jempasam.hexlink.operators.string.OpGetNbt
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.util.Identifier

object HexlinkPatterns {

    private fun register(id: String, pattern: HexPattern, op : Action){
        HexPattern.
        PatternRegistry.mapPattern(pattern, Identifier(HexlinkMod.MODID,id), op)
    }

    private fun registerFor(modid: String, id: String, pattern: HexPattern, op: ()->()->Action){
        if(FabricLoader.getInstance().isModLoaded(modid)){
            PatternRegistry.mapPattern(pattern, Identifier(HexlinkMod.MODID,id), op()())
        }
        else PatternRegistry.mapPattern(pattern, Identifier(HexlinkMod.MODID,id), OpStub(modid))
    }

    fun registerAll(){

        register("spirit_extraction",
                HexPattern.fromAngles("aawddaweddwaa",HexDir.NORTH_EAST),
                OpExtractSpirit()
        )

        register("spirit_transfer",
                HexPattern.fromAngles("aawddawdaqqqa",HexDir.NORTH_EAST),
                OpSpiritTransfer()
        )
        register("spirit_transfer_look",
                HexPattern.fromAngles("aawddaeqqqqq",HexDir.NORTH_EAST),
                OpGetSpiritIota(false)
        )

        register("spirit_self_transfer_look",
                HexPattern.fromAngles("aawddaeqqqqqaw",HexDir.NORTH_EAST),
                OpGetSpiritIota(true)
        )


        register("spirit_transfer_count",
                HexPattern.fromAngles("aawddaeqq",HexDir.NORTH_EAST),
                OpSpiritCountIota(false)
        )

        register("spirit_self_transfer_count",
                HexPattern.fromAngles("aawddaeqqaw",HexDir.NORTH_EAST),
                OpSpiritCountIota(true)
        )

        register("spirit_vortex",
                HexPattern.fromAngles("aawddaweaqa",HexDir.NORTH_EAST),
                OpVortexSpirit()
        )

        register("spirit_manifestation",
                HexPattern.fromAngles("aawddaweqaeaq",HexDir.NORTH_EAST),
                OpManisfestSpirit(false)
        )

        register("spirit_self_manifestation",
                HexPattern.fromAngles("aawddaweqaeaqa",HexDir.NORTH_EAST),
                OpManisfestSpirit(true)
        )

        register("spirit_look",
                HexPattern.fromAngles("aawddaweqqqqq",HexDir.NORTH_EAST),
                OpSpiritTest()
        )


        register("write_spell",
                HexPattern.fromAngles("waaddaawwwddaaddwaweqqqqqwaeaeaeaeaeaqwwwewddaaddwwwaaddaaw",HexDir.NORTH_EAST),
                OpFillSpell(MediaConstants.CRYSTAL_UNIT)
        )

        registerFor("moreiotas", "get_nbt",
            HexPattern.fromAngles("adaada", HexDir.SOUTH_WEST),
            {{OpGetNbt}}
        )
    }
}