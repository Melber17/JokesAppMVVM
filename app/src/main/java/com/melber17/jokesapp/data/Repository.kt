package com.melber17.jokesapp.data

import com.melber17.jokesapp.data.cache.JokeResult
import com.melber17.jokesapp.presentation.JokeUICallback
import com.melber17.jokesapp.presentation.JokeUi

interface Repository<S, E> {

    fun fetch(): JokeResult
    fun changeJokeStatus(): JokeUi
    fun chooseFavorites(isFavorite: Boolean)
}

