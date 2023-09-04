package com.melber17.jokesapp.data.cache

import com.melber17.jokesapp.data.Error
import com.melber17.jokesapp.data.cloud.JokeCloud
import com.melber17.jokesapp.presentation.JokeUi
import com.melber17.jokesapp.presentation.ManageResources
import io.realm.Realm
import kotlin.random.Random

interface CacheDataSource {
    fun addOrRemove(id: Int, joke: JokeCloud): JokeUi
    fun fetch(jokeCacheCallback: JokeCacheCallback)

    class Base(private val realm: ProvideRealm, manageResources: ManageResources) :
        CacheDataSource {
        private val error by lazy {
            Error.NoFavoriteJoke(manageResources)
        }

        override fun addOrRemove(id: Int, joke: JokeCloud): JokeUi {
            realm.provideRealm().let {
                val jokeCached = it.where(JokeCache::class.java).equalTo("id", id).findFirst()
                if (jokeCached == null) {
                    it.executeTransaction { realm ->
                        val jokeCache = joke.toCache()
                        realm.insert(jokeCache)
                    }

                    return joke.toFavoriteUi()
                } else {
                    it.executeTransaction { realm ->
                        realm.where(JokeCache::class.java).equalTo("id", id).findFirst()
                            ?.deleteFromRealm()
                    }

                    return joke.toUi()
                }
            }
        }

        override fun fetch(jokeCacheCallback: JokeCacheCallback) {
            realm.provideRealm().let {
                val jokes = it.where(JokeCache::class.java).findAll()
                if (jokes.isEmpty()) {
                    jokeCacheCallback.provideError(error)
                } else {
                    val jokeCached = jokes.random()
                    jokeCacheCallback.provideJoke(
                        JokeCloud(
                            jokeCached.type,
                            jokeCached.text,
                            jokeCached.punchline,
                            jokeCached.id
                        )
                    )
                }
            }
        }

    }

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

interface ProvideRealm {
    fun provideRealm(): Realm
}