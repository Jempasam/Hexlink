package jempasam.hexlink.data

import com.google.gson.JsonObject
import jempasam.hexlink.HexlinkRegistry
import jempasam.hexlink.spirit.extractor.NodeExtractor
import net.minecraft.util.Identifier

object SpecialExtractorDataLoader: JsonEntryDataLoader("extractors") {

    override fun apply(id: Identifier, obj: JsonObject) {
        val extractor=NodeExtractor.parse(obj)
        HexlinkRegistry.register(HexlinkRegistry.EXTRACTOR, id, extractor)
    }

}