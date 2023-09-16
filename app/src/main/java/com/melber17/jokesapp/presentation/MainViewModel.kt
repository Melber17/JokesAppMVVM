package com.melber17.jokesapp.presentation

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melber17.jokesapp.data.Error
import com.melber17.jokesapp.data.Joke
import com.melber17.jokesapp.data.Repository
import com.melber17.jokesapp.data.ToBaseUi
import com.melber17.jokesapp.data.ToFavoriteUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainViewModel(
    private val repository: Repository<JokeUi, Error>,
    private val toFavorite: Joke.Mapper<JokeUi> = ToFavoriteUi(),
    private val toBaseUi: Joke.Mapper<JokeUi> = ToBaseUi()
) : ViewModel() {
    private var jokeUICallback: JokeUICallback = JokeUICallback.Empty()
    private lateinit var job: Job

     fun getJoke() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.fetch()
            val ui = if (result.isSuccessful()) {
                result.map(if (result.toFavorite()) toFavorite else toBaseUi)
            } else {
                JokeUi.Failed(result.errorMessage())
            }

            withContext(Dispatchers.Main) {
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
        viewModelScope.launch(Dispatchers.IO) {
            val jokeUi = repository.changeJokeStatus()
            withContext(Dispatchers.Main) {
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