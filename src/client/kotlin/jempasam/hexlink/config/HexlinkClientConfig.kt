package assets.hexlink.config

import com.google.gson.JsonObject
import jempasam.hexlink.config.Config


object HexlinkClientConfig : Config {
    var colored_casting: Boolean=true

    override fun load(input: JsonObject) {
        colored_casting=input.get("colored_casting")?.asBoolean ?: true
    }

    override fun save(): JsonObject {
        return JsonObject().apply {
            addProperty("colored_casting", colored_casting)
        }
    }
}