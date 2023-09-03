package com.melber17.jokesapp.data.cache

import com.melber17.jokesapp.data.Error
import com.melber17.jokesapp.data.cloud.JokeCloud
import com.melber17.jokesapp.presentation.JokeUi
import com.melber17.jokesapp.presentation.ManageResources
import kotlin.random.Random

interface CacheDataSource {
    fun addOrRemove(id: Int, joke: JokeCloud): JokeUi
    fun fetch(jokeCacheCallback: JokeCacheCallback)

    class Fake(private val manageResources: ManageResources) : CacheDataSource {

        private val map = mutableMapOf<Int, JokeCloud>()
        private val error by lazy {
            Error.NoFavoriteJoke(manageResources)
        }
        override fun addOrRemove(id: Int, joke: JokeCloud): JokeUi {
            return if (map.containsKey(id)) {
                map.remove(id)
                joke.toUi()
            } else {
                map[id] = joke
                joke.toFavoriteUi()
            }
        }

        private var count = 0
        override fun fetch(jokeCacheCallback: JokeCacheCallback) {
            if (map.isEmpty()) {
                jokeCacheCallback.provideError(error)
            } else {
                if (++count == map.size) {
                    count = 0
                }
                jokeCacheCallback.provideJoke(
                    map.toList()[count].second
                )
            }

        }

    }
}

interface JokeCacheCallback : ProvideError {
    fun provideJoke(joke: JokeCloud)
}

interface ProvideError {
    fun provideError(error: Error)
}