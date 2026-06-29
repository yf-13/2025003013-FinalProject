package com.example.studyflash.data.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DictionaryResponse(
    @Json(name = "word")
    val word: String?,
    @Json(name = "meaning")
    val meaning: Meaning?
)

@JsonClass(generateAdapter = true)
data class Meaning(
    @Json(name = "noun")
    val noun: List<Definition>?,
    @Json(name = "verb")
    val verb: List<Definition>?
)

@JsonClass(generateAdapter = true)
data class Definition(
    @Json(name = "definition")
    val definition: String?,
    @Json(name = "example")
    val example: String?
)