package com.melber17.jokesapp

import retrofit2.Call
import retrofit2.http.GET

interface JokeService  {

    @GET("random_joke")
    fun joke(): Call<JokeCloud>
}

interface ServiceCallback {
    fun returnSuccess(data: JokeCloud)

    fun returnError(errorType: ErrorType)
}

enum class ErrorType {
    NO_CONNECTION,
    OTHER
}