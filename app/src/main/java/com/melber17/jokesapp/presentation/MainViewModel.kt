package com.melber17.jokesapp.presentation

import androidx.annotation.DrawableRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainViewModel(
    private val communication: JokeCommunication,
    private val repository: Repository<JokeUi, Error>,
    private val toFavorite: Joke.Mapper<JokeUi> = ToFavoriteUi(),
    private val toBaseUi: Joke.Mapper<JokeUi> = ToBaseUi(),
    dispatchersList: DispatcherList = DispatcherList.Base(),
) : BaseViewModel(dispatchersList), Observe<JokeUi> {

    private val blockUi: suspend (JokeUi) -> Unit = {
        communication.map(it)
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<JokeUi>) {
        communication.observe(owner, observer)
    }

    fun getJoke() = super.handle({
        val result = repository.fetch()
        if (result.isSuccessful()) {
            result.map(if (result.toFavorite()) toFavorite else toBaseUi)
        } else {
            JokeUi.Failed(result.errorMessage())
        }
    }, blockUi)


    fun chooseFavorite(isFavorite: Boolean) {
        repository.chooseFavorites(isFavorite)
    }

    fun changeJokeStatus() = handle({
        repository.changeJokeStatus()
    }, blockUi)
}

interface Observe<T: Any> {
    fun observe(owner: LifecycleOwner, observer: Observer<T>) = Unit
}

interface Communication <T: Any> : Observe<T> {
        fun map(data: T)

        abstract class Abstract<T: Any>(private val liveData: MutableLiveData<T> = MutableLiveData()): Communication<T> {
            override fun map(data: T) {
              liveData.value = data
            }

            override fun observe(owner: LifecycleOwner, observer: Observer<T>) {
               liveData.observe(owner, observer)
            }
        }
}

interface JokeCommunication: Communication<JokeUi> {
    class Base: Communication.Abstract<JokeUi>(), JokeCommunication
}

interface JokeUICallback {
    fun provideText(text: String)
    fun provideIconResId(@DrawableRes iconResId: Int)
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