package com.melber17.jokesapp

import org.junit.Assert.*
import org.junit.Test

private const val SUCCESS_MESSAGE = "fake joke"
private const val ERROR_MESSAGE = "fake error"

class MainViewModelTest {
    @Test
    fun test_success() {
        val model = FakeModel()
        model.returnSuccess = true
        val viewModel = MainViewModel(model)
        viewModel.init(object : TextCallback {
            override fun provideText(text: String) {
                assertEquals("$SUCCESS_MESSAGE\npunchline", text)
            }

        })
        viewModel.getJoke()
    }

    @Test
    fun test_error() {
        val model = FakeModel()
        model.returnSuccess = false
        val viewModel = MainViewModel(model)
        viewModel.init(object : TextCallback {
            override fun provideText(text: String) {
                assertEquals(ERROR_MESSAGE, text)
            }

        })
        viewModel.getJoke()
    }
}

private class FakeModel() : Model<Joke, Error> {
    var returnSuccess = true
    private var callback: ResultCallback<Joke, Error>? = null
    override fun fetch() {
        if (returnSuccess) {
            callback?.provideSuccess(Joke(SUCCESS_MESSAGE, "punchline"))
        } else {
            callback?.provideError(FakeError())
        }
    }

    override fun clear() {
        callback = null
    }

    override fun init(resultCallback: ResultCallback<Joke, Error>) {
        callback = resultCallback
    }

}

private class FakeError() : Error {
    override fun message(): String {
        return ERROR_MESSAGE
    }

}