package com.melber17.jokesapp.data

import com.melber17.jokesapp.data.cache.CacheDataSource
import com.melber17.jokesapp.data.cloud.CloudDataSource
import com.melber17.jokesapp.data.cloud.JokeCloud
import com.melber17.jokesapp.data.cloud.JokeCloudCallback
import com.melber17.jokesapp.presentation.JokeUi

class BaseRepository(
    private val cloudDataSource: CloudDataSource,
    private val cacheDataSource: CacheDataSource
) : Repository<JokeUi, Error> {
    private var callback: ResultCallback<JokeUi, Error>? = null
    private var jokeCloudCached: JokeCloud? = null

    override fun fetch() {
        cloudDataSource.fetch(object : JokeCloudCallback {
            override fun provideJokeCloud(jokeCloud: JokeCloud) {
                jokeCloudCached = jokeCloud
                callback?.provideSuccess(jokeCloud.toUi())
            }

            override fun provideError(error: Error) {
                jokeCloudCached = null
                callback?.provideError(error)
            }

        })
    }

    override fun clear() {
        callback = null
    }

    override fun changeJokeStatus(resultCallback: ResultCallback<JokeUi, Error>) {
        jokeCloudCached?.let {
            resultCallback.provideSuccess(it.change(cacheDataSource))
        }
    }

    override fun init(resultCallback: ResultCallback<JokeUi, Error>) {
        callback = resultCallback
    }
}
