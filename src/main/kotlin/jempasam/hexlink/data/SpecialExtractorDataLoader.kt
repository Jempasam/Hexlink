package jempasam.hexlink.data

import com.google.gson.JsonObject
import jempasam.hexlink.HexlinkRegistry
import jempasam.hexlink.spirit.extractor.SpecialExtractor
import net.minecraft.util.Identifier

object SpecialExtractorDataLoader: JsonEntryDataLoader("extractors") {

    override fun apply(id: Identifier, obj: JsonObject) {
        val extractor=SpecialExtractor.parse(obj)
        HexlinkRegistry.register(HexlinkRegistry.SPIRIT_EXTRACTOR, id, extractor)
    }

}