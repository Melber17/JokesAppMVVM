package com.melber17.jokesapp.presentation

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melber17.jokesapp.data.Error
import com.melber17.jokesapp.data.Joke
import com.melber17.jokesapp.data.Repository
import com.melber17.jokesapp.data.ToBaseUi
import com.melber17.jokesapp.data.ToFavoriteUi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainViewModel(
    private val repository: Repository<JokeUi, Error>,
    private val toFavorite: Joke.Mapper<JokeUi> = ToFavoriteUi(),
    private val toBaseUi: Joke.Mapper<JokeUi> = ToBaseUi(),
    private val dispatchersList: DispatcherList = DispatcherList.Base()
) : ViewModel() {
    private var jokeUICallback: JokeUICallback = JokeUICallback.Empty()

     fun getJoke() {
        viewModelScope.launch(dispatchersList.io()) {
            val result = repository.fetch()
            val ui = if (result.isSuccessful()) {
                result.map(if (result.toFavorite()) toFavorite else toBaseUi)
            } else {
                JokeUi.Failed(result.errorMessage())
            }

            withContext(dispatchersList.ui()) {
                ui.show(jokeUICallback)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        jokeUICallback = JokeUICallback.Empty()
    }

    fun init(jokeUICallback: JokeUICallback) {
        this.jokeUICallback = jokeUICallback
    }

    fun chooseFavorite(isFavorite: Boolean) {
        repository.chooseFavorites(isFavorite)
    }

    fun changeJokeStatus() {
        viewModelScope.launch(dispatchersList.io()) {
            val jokeUi = repository.changeJokeStatus()
            withContext(dispatchersList.ui()) {
                jokeUi.show(jokeUICallback)
            }
        }
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

interface DispatcherList {
    fun io(): CoroutineDispatcher
    fun ui(): CoroutineDispatcher

    class Base: DispatcherList {
        override fun io() = Dispatchers.IO
        override fun ui() = Dispatchers.Main
    }
}