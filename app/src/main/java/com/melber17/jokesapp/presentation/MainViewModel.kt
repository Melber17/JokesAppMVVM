package com.melber17.jokesapp.presentation

import androidx.annotation.DrawableRes
import com.melber17.jokesapp.data.Error
import com.melber17.jokesapp.data.Joke
import com.melber17.jokesapp.data.Repository
import com.melber17.jokesapp.data.ToBaseUi
import com.melber17.jokesapp.data.ToFavoriteUi


class MainViewModel(
    private val repository: Repository<JokeUi, Error>,
    private val toFavorite: Joke.Mapper<JokeUi> = ToFavoriteUi(),
    private val toBaseUi: Joke.Mapper<JokeUi> = ToBaseUi()
) {
    private var jokeUICallback: JokeUICallback = JokeUICallback.Empty()


    fun getJoke() =
        Thread {
            val result = repository.fetch()
            if (result.isSuccessful()) {
                result.map(if (result.toFavorite()) toFavorite else toBaseUi).show(jokeUICallback)
            } else {
                JokeUi.Failed(result.errorMessage()).show(jokeUICallback)
            }
        }.start()

    fun clear() {
        jokeUICallback = JokeUICallback.Empty()
    }

    fun init(jokeUICallback: JokeUICallback) {
        this.jokeUICallback = jokeUICallback
    }

    fun chooseFavorite(isFavorite: Boolean) {
        repository.chooseFavorites(isFavorite)
    }

    fun changeJokeStatus() {
        Thread {
            val jokeUi = repository.changeJokeStatus()
            jokeUi.show(jokeUICallback)
        }.start()
    }
}

interface JokeUICallback {
    fun provideText(text: String)
    fun provideIconResId(@DrawableRes iconResId: Int)

    class Empty : JokeUICallback {
        override fun provideText(text: String) = Unit
        override fun provideIconResId(iconResId: Int) = Unit
    }
}