package com.melber17.jokesapp.data.cache

import com.melber17.jokesapp.data.Error
import com.melber17.jokesapp.data.Joke
import com.melber17.jokesapp.data.ToBaseUi
import com.melber17.jokesapp.data.ToCache
import com.melber17.jokesapp.data.ToFavoriteUi
import com.melber17.jokesapp.presentation.JokeUi
import com.melber17.jokesapp.presentation.ManageResources
import io.realm.Realm
import java.lang.IllegalStateException

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
            val instanceRealm =  realm.provideRealm()

                val jokeCached = instanceRealm.where(JokeCache::class.java).equalTo("id", id).findFirst()
            return if (jokeCached == null) {
                instanceRealm.executeTransaction {
                    val jokeCache = joke.map(mapper)
                    it.insert(jokeCache)
                }
                joke.map(favoriteUi)
            } else {
                instanceRealm.executeTransaction {
                    it.where(JokeCache::class.java).equalTo("id", id).findFirst()
                        ?.deleteFromRealm()
                }
                joke.map(baseUi)
            }
        }

        override fun fetch(): JokeResult {
            val realm = realm.provideRealm()
            val jokes = realm.where(JokeCache::class.java).findAll()
            return if (jokes.isEmpty()) {
                JokeResult.Failure(error)
            } else {
                JokeResult.Success(realm.copyFromRealm(jokes.random()), true)
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
        override fun fetch(): JokeResult {

            return if (map.isEmpty()) {
                JokeResult.Failure(error)
            } else {
                if (++count == map.size) {
                    count = 0
                }
                JokeResult.Success(map.toList()[count].second, true)
            }

        }

    }
}

interface DataSource {
    fun fetch(): JokeResult
}

interface JokeResult : Joke {

    fun toFavorite(): Boolean
    fun isSuccessful(): Boolean

    fun errorMessage(): String

    class Success(private val joke: Joke, private val toFavorite: Boolean) : JokeResult {
        override fun toFavorite(): Boolean = toFavorite
        override fun isSuccessful(): Boolean = true
        override fun errorMessage() = ""
        override fun <T> map(mapper: Joke.Mapper<T>): T = joke.map(mapper)
    }

    class Failure(private val error: Error) : JokeResult {
        override fun toFavorite(): Boolean = false
        override fun isSuccessful(): Boolean = false
        override fun errorMessage() = error.message()
        override fun <T> map(mapper: Joke.Mapper<T>): T = throw IllegalStateException()
    }
}

interface ProvideRealm {
    fun provideRealm(): Realm
}