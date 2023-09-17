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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainViewModel(
    private val repository: Repository<JokeUi, Error>,
    private val toFavorite: Joke.Mapper<JokeUi> = ToFavoriteUi(),
    private val toBaseUi: Joke.Mapper<JokeUi> = ToBaseUi(),
    dispatchersList: DispatcherList = DispatcherList.Base(),
) : BaseViewModel(dispatchersList) {
    private var jokeUICallback: JokeUICallback = JokeUICallback.Empty()
    private val blockUi: suspend (JokeUi) -> Unit = {
        it.show(jokeUICallback)
    }

    fun getJoke() = super.handle({
        val result = repository.fetch()
        if (result.isSuccessful()) {
            result.map(if (result.toFavorite()) toFavorite else toBaseUi)
        } else {
            JokeUi.Failed(result.errorMessage())
        }
    }, blockUi)

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

    fun changeJokeStatus() = handle({
        repository.changeJokeStatus()
    }, blockUi)
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

    class Base : DispatcherList {
        override fun io() = Dispatchers.IO
        override fun ui() = Dispatchers.Main
    }
}

interface HandleUi {
    fun handle(
        coroutineScope: CoroutineScope,
        jokeUICallback: JokeUICallback,
        block: suspend () -> JokeUi
    )


    class Base(private val dispatchersList: DispatcherList) : HandleUi {
        override fun handle(
            coroutineScope: CoroutineScope,
            jokeUICallback: JokeUICallback,
            block: suspend () -> JokeUi
        ) {
            coroutineScope.launch(dispatchersList.io()) {
                val jokeUi = block.invoke()
                withContext(dispatchersList.ui()) {
                    jokeUi.show(jokeUICallback)
                }
            }
        }


    }
}

abstract class BaseViewModel(private val dispatchersList: DispatcherList) : ViewModel() {
    fun <T> handle(
        blockIo: suspend () -> T,
        blockUi: suspend (T) -> Unit
    ) = viewModelScope.launch(dispatchersList.io()) {
        val result = blockIo.invoke()
        withContext(dispatchersList.ui()) {
            blockUi.invoke(result)
        }
    }
}