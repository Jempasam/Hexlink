package jempasam.hexlink.config

import com.google.gson.JsonObject

interface Config {
    fun load(input: JsonObject)

    fun save(): JsonObject
}