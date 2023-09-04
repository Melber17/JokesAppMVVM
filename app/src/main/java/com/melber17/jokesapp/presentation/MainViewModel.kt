package com.melber17.jokesapp.presentation

import androidx.annotation.DrawableRes
import com.melber17.jokesapp.data.Error
import com.melber17.jokesapp.data.Repository
import com.melber17.jokesapp.data.ResultCallback


class MainViewModel(private val repository: Repository<JokeUi, Error>) {
    private var jokeUICallback: JokeUICallback = JokeUICallback.Empty()

    private val resultCallback = object : ResultCallback<JokeUi, Error> {
        override fun provideSuccess(data: JokeUi) = data.show(jokeUICallback)
        override fun provideError(error: Error) = JokeUi.Failed(error.message()).show(jokeUICallback)
    }

    fun getJoke() {
        repository.fetch()
    }

    fun clear() {
        jokeUICallback = JokeUICallback.Empty()
        repository.clear()
    }

    fun init(jokeUICallback: JokeUICallback) {
        this.jokeUICallback = jokeUICallback
        repository.init(resultCallback)
    }

    fun chooseFavorite(isFavorite: Boolean) {
        repository.chooseFavorites(isFavorite)
    }

    fun changeJokeStatus() {
      Thread {
          repository.changeJokeStatus(resultCallback)
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