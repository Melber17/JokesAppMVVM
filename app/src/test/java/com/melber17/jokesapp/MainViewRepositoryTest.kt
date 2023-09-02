package com.melber17.jokesapp

import com.melber17.jokesapp.data.Error
import com.melber17.jokesapp.data.Repository
import com.melber17.jokesapp.data.ResultCallback
import com.melber17.jokesapp.presentation.JokeUi
import com.melber17.jokesapp.presentation.MainViewModel
import com.melber17.jokesapp.presentation.JokeUICallback
import org.junit.Assert.*
import org.junit.Test

private const val SUCCESS_MESSAGE = "fake joke"
private const val ERROR_MESSAGE = "fake error"

class MainViewRepositoryTest {
    @Test
    fun test_success() {
        val model = com.melber17.jokesapp.data.FakeRepository()
        model.returnSuccess = true
        val viewModel = MainViewModel(model)
        viewModel.init(object : JokeUICallback {
            override fun provideText(text: String) {
                assertEquals("$SUCCESS_MESSAGE\npunchline", text)
            }

            override fun provideIconResId(iconResId: Int) {
                assertEquals(R.drawable.ic_favorite_filled_48, iconResId)
            }

        })
        viewModel.getJoke()
    }

    @Test
    fun test_error() {
        val model = com.melber17.jokesapp.data.FakeRepository()
        model.returnSuccess = false
        val viewModel = MainViewModel(model)
        viewModel.init(object : JokeUICallback {
            override fun provideText(text: String) {
                assertEquals(ERROR_MESSAGE, text)
            }

            override fun provideIconResId(iconResId: Int) {
                assertEquals(R.drawable.ic_favorite_empty_48, iconResId)
            }
        })
        viewModel.getJoke()
    }
}

private class FakeRepository() : Repository<JokeUi, Error> {
    var returnSuccess = true
    private var callback: ResultCallback<JokeUi, Error>? = null
    override fun fetch() {
        if (returnSuccess) {
            callback?.provideSuccess(JokeUi.Base(SUCCESS_MESSAGE, "punchline"))
        } else {
            callback?.provideError(FakeError())
        }
    }

    override fun clear() {
        callback = null
    }

    override fun init(resultCallback: ResultCallback<JokeUi, Error>) {
        callback = resultCallback
    }

}

private class FakeError() : Error {
    override fun message(): String {
        return ERROR_MESSAGE
    }

}