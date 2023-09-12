package com.melber17.jokesapp.data

import com.melber17.jokesapp.data.cache.CacheDataSource
import com.melber17.jokesapp.data.cache.JokeResult
import com.melber17.jokesapp.data.cloud.CloudDataSource
import com.melber17.jokesapp.presentation.JokeUi

class BaseRepository(
    private val cloudDataSource: CloudDataSource,
    private val cacheDataSource: CacheDataSource,
    private val change: Joke.Mapper<JokeUi> = Change(cacheDataSource),

    ) : Repository<JokeUi, Error> {
    private var jokeTemporary: Joke? = null
    private var getJokeFromCache = false
    override fun fetch(): JokeResult {
        val jokeResult = if (getJokeFromCache) {
            cacheDataSource.fetch()
        } else
            cloudDataSource.fetch()
        jokeTemporary = if (jokeResult.isSuccessful()) {
            jokeResult.map(ToDomain())
        } else
            null
        return jokeResult
    }


    override fun changeJokeStatus(): JokeUi {
        return jokeTemporary!!.map(change)
    }

    override fun chooseFavorites(isFavorite: Boolean) {
        getJokeFromCache = isFavorite
    }

}
