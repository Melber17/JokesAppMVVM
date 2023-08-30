package com.melber17.jokesapp

import androidx.annotation.DrawableRes


class MainViewModel(private val model: Model<Joke, Error>) {
    private var textCallback: TextCallback = TextCallback.Empty()

    private val resultCallback = object : ResultCallback<Joke, Error> {
        override fun provideSuccess(data: Joke) = data.show(textCallback)
        override fun provideError(error: Error) = Joke.Failed(error.message()).show(textCallback)
    }

    fun getJoke() {
        model.fetch()
    }

    fun clear() {
        textCallback = TextCallback.Empty()
        model.clear()
    }

    fun init(textCallback: TextCallback) {
        this.textCallback = textCallback
        model.init(resultCallback)
    }

    fun changeFavorite(isChecked: Boolean) {

    }

}

interface TextCallback {
    fun provideText(text: String)
    fun provideIconResId(@DrawableRes iconResId: Int)

    class Empty : TextCallback {
        override fun provideText(text: String) = Unit
        override fun provideIconResId(iconResId: Int) = Unit
    }
}