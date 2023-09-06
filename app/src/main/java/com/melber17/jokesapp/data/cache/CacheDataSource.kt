package com.melber17.jokesapp.data.cache

import com.melber17.jokesapp.data.Error
import com.melber17.jokesapp.data.Joke
import com.melber17.jokesapp.data.ToBaseUi
import com.melber17.jokesapp.data.ToCache
import com.melber17.jokesapp.data.ToFavoriteUi
import com.melber17.jokesapp.presentation.JokeUi
import com.melber17.jokesapp.presentation.ManageResources
import io.realm.Realm

interface CacheDataSource : DataSource {
    fun addOrRemove(id: Int, joke: Joke): JokeUi
    class Base(
        private val realm: ProvideRealm,
        manageResources: ManageResources,
        private val error: Error = Error.NoFavoriteJoke(manageResources),
        private val mapper: Joke.Mapper<JokeCache> = ToCache(),
        private val baseUi: Joke.Mapper<JokeUi> = ToBaseUi(),
        private val favoriteUi: Joke.Mapper<JokeUi> = ToFavoriteUi()
    ) : CacheDataSource {

        override fun addOrRemove(id: Int, joke: Joke): JokeUi {
            realm.provideRealm().let {
                val jokeCached = it.where(JokeCache::class.java).equalTo("id", id).findFirst()
                if (jokeCached == null) {
                    it.executeTransaction { realm ->
                        val jokeCache = joke.map(mapper)
                        realm.insert(jokeCache)
                    }

                    return joke.map(favoriteUi)
                } else {
                    it.executeTransaction { realm ->
                        realm.where(JokeCache::class.java).equalTo("id", id).findFirst()
                            ?.deleteFromRealm()
                    }

                    return joke.map(baseUi)
                }
            }
        }

        override fun fetch(jokeCallback: JokeCallback) {
            realm.provideRealm().let {
                val jokes = it.where(JokeCache::class.java).findAll()
                if (jokes.isEmpty()) {
                    jokeCallback.provideError(error)
                } else {
                    val jokeCached = jokes.random()
                    jokeCallback.provideJoke(it.copyFromRealm(jokeCached))
                }
            }
        }

    }

    class Fake(private val manageResources: ManageResources) : CacheDataSource {

        private val map = mutableMapOf<Int, Joke>()
        private val error by lazy {
            Error.NoFavoriteJoke(manageResources)
        }

        override fun addOrRemove(id: Int, joke: Joke): JokeUi {
            return if (map.containsKey(id)) {
                map.remove(id)
                joke.map(ToBaseUi())
            } else {
                map[id] = joke
                joke.map(ToFavoriteUi())
            }
        }

        private var count = 0
        override fun fetch(jokeCallback: JokeCallback) {
            if (map.isEmpty()) {
                jokeCallback.provideError(error)
            } else {
                if (++count == map.size) {
                    count = 0
                }
                jokeCallback.provideJoke(
                    map.toList()[count].second
                )
            }

        }

    }
}

interface DataSource {
    fun fetch(jokeCallback: JokeCallback)
}

interface JokeCallback : ProvideError {
    fun provideJoke(joke: Joke)
}

interface ProvideError {
    fun provideError(error: Error)
}

interface ProvideRealm {
    fun provideRealm(): Realm
}