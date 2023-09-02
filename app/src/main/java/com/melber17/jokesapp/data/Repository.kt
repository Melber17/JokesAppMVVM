package com.melber17.jokesapp.data

import com.melber17.jokesapp.presentation.JokeUICallback
import com.melber17.jokesapp.presentation.JokeUi

interface Repository<S, E> {

    fun fetch()
    fun clear()
    fun init(callback: ResultCallback<S, E>)
    fun changeJokeStatus(resultCallback: ResultCallback<JokeUi, Error>)
}

interface ResultCallback<S, E> {
    fun provideSuccess(data: S)

    fun provideError(error: E)
}