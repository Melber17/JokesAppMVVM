package com.melber17.jokesapp.data.cloud

import com.google.gson.annotations.SerializedName
import com.melber17.jokesapp.data.Joke

data class JokeCloud(
    @SerializedName("type")
    private val type: String,
    @SerializedName("setup")
    private val mainText: String,
    @SerializedName("punchline")
    private val punchline: String,
    @SerializedName("id")
    private val id: Int
): Joke {
    override suspend fun <T> map(mapper: Joke.Mapper<T>): T = mapper.map(type, mainText, punchline, id)
}