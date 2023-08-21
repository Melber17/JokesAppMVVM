package com.melber17.jokesapp

import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.UnknownHostException

interface JokeService  {
    fun joke(callback: ServiceCallback)

    class Base: JokeService {
        override fun joke(callback: ServiceCallback) {
           Thread {
               var connection: HttpURLConnection? = null
               try {
                   val url = URL(URL)
                   connection = url.openConnection() as HttpURLConnection
                   InputStreamReader(BufferedInputStream(connection.inputStream)).use {
                       val text = it.readText()
                       callback.returnSuccess(text)
                   }
               } catch (err: Exception) {
                   if (err is UnknownHostException || err is java.net.ConnectException) {
                       callback.returnError(ErrorType.NO_CONNECTION)
                   } else {
                       callback.returnError(ErrorType.OTHER)
                   }
               } finally {
                   connection?.disconnect()
               }
           }.start()
        }

        companion object {
            private const val URL = "https://official-joke-api.appspot.com/random_joke"
        }

    }
}

interface ServiceCallback {
    fun returnSuccess(data: String)

    fun returnError(errorType: ErrorType)
}

enum class ErrorType {
    NO_CONNECTION,
    OTHER
}