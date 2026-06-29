package com.example.studyflash.data.network

import com.example.studyflash.data.network.dto.DictionaryResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("api/v1/entries/en/{word}")
    suspend fun getWordDefinition(
        @Path("word") word: String
    ): List<DictionaryResponse>
}