package com.melber17.jokesapp.data.cache

import com.melber17.jokesapp.data.cloud.JokeCloud
import com.melber17.jokesapp.presentation.JokeUi

interface CacheDataSource {
    fun addOrRemove(id: Int, joke: JokeCloud): JokeUi

    class Fake : CacheDataSource {
        private var count = 0

        override fun addOrRemove(id: Int, joke: JokeCloud): JokeUi {
            return if (++count % 2 == 0) {
                joke.toUi()
            } else {
                joke.toFavoriteUi()
            }
        }

    }
}