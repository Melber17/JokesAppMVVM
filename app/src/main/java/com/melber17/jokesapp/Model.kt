package com.melber17.jokesapp

interface Model<S, E> {

    fun fetch()
    fun clear()
    fun init(callback: ResultCallback<S, E>)
}

interface ResultCallback<S, E> {
    fun provideSuccess(data: S)

    fun provideError(error: E)
}