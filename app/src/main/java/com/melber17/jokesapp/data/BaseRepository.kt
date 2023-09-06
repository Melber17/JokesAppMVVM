package com.melber17.jokesapp.data

import com.melber17.jokesapp.data.cache.CacheDataSource
import com.melber17.jokesapp.data.cache.JokeCallback
import com.melber17.jokesapp.data.cloud.CloudDataSource
import com.melber17.jokesapp.presentation.JokeUi

class BaseRepository(
    private val cloudDataSource: CloudDataSource,
    private val cacheDataSource: CacheDataSource,
    private val change: Joke.Mapper<JokeUi> = Change(cacheDataSource)
) : Repository<JokeUi, Error> {
    private var callback: ResultCallback<JokeUi, Error>? = null
    private var jokeTemporary: Joke? = null
    private var getJokeFromCache = false
    private val toFavorite = ToFavoriteUi()
    private val toBaseUi = ToBaseUi()
    private val jokeCacheCallback = BaseJokeCallback(toFavorite)
    private val cloudCallback = BaseJokeCallback(toBaseUi)
    override fun fetch() {
        if (getJokeFromCache) {
            cacheDataSource.fetch(jokeCacheCallback)
        } else
            cloudDataSource.fetch(cloudCallback)
    }

    private inner class BaseJokeCallback(
        private val mapper: Joke.Mapper<JokeUi>
    ) : JokeCallback {
        override fun provideJoke(joke: Joke) {
            jokeTemporary = joke
            callback?.provideSuccess(joke.map(mapper))
        }

        override fun provideError(error: Error) {
            jokeTemporary = null
            callback?.provideError(error)
        }

    }

    override fun clear() {
        callback = null
    }

    override fun changeJokeStatus(resultCallback: ResultCallback<JokeUi, Error>) {
        jokeTemporary?.let {
            resultCallback.provideSuccess(it.map(change))
        }
    }

    override fun chooseFavorites(isFavorite: Boolean) {
        getJokeFromCache = isFavorite
    }

    override fun init(resultCallback: ResultCallback<JokeUi, Error>) {
        callback = resultCallback
    }

}
