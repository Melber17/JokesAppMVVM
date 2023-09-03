package com.melber17.jokesapp.data

import android.annotation.SuppressLint
import com.melber17.jokesapp.data.cache.CacheDataSource
import com.melber17.jokesapp.data.cache.JokeCacheCallback
import com.melber17.jokesapp.data.cloud.CloudDataSource
import com.melber17.jokesapp.data.cloud.JokeCloud
import com.melber17.jokesapp.data.cloud.JokeCloudCallback
import com.melber17.jokesapp.presentation.JokeUi
import com.melber17.jokesapp.presentation.ManageResources

class BaseRepository(
    private val cloudDataSource: CloudDataSource,
    private val cacheDataSource: CacheDataSource,
    private val manageResources: ManageResources
) : Repository<JokeUi, Error> {
    private var callback: ResultCallback<JokeUi, Error>? = null
    private var jokeCloudCached: JokeCloud? = null
    private var getJokeFromCache = false

    override fun fetch() {
        if (getJokeFromCache) {
          cacheDataSource.fetch(object: JokeCacheCallback {
               override fun provideJoke(joke: JokeCloud) {
                   callback?.provideSuccess(joke.toFavoriteUi())
               }

              override fun provideError(error: Error) {
                  callback?.provideError(error)
              }
           })
        } else
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


    override fun chooseFavorites(isFavorite: Boolean) {
        getJokeFromCache = isFavorite
    }



    override fun init(resultCallback: ResultCallback<JokeUi, Error>) {
        callback = resultCallback
    }

}
