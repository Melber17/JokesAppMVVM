package com.melber17.jokesapp


class MainViewModel(private val model: Model<Joke, Error>) {
    private var textCallback: TextCallback = TextCallback.Empty()
    fun getJoke() {
        model.fetch()
    }

    fun clear() {
        textCallback = TextCallback.Empty()
        model.clear()
    }

    fun init(textCallback: TextCallback) {
        this.textCallback = textCallback
        model.init(object: ResultCallback<Joke, Error> {
            override fun provideSuccess(data: Joke) {
              textCallback.provideText(data.toUi())
            }
            override fun provideError(error: Error) {
             textCallback.provideText(error.message())
            }

        })
    }

}

interface TextCallback {
    fun provideText(text: String)

    class Empty : TextCallback {
        override fun provideText(text: String) = Unit
    }
}