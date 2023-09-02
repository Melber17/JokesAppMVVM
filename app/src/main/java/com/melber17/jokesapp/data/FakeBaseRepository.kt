package com.melber17.jokesapp.data

import com.melber17.jokesapp.presentation.ManageResources
import com.melber17.jokesapp.presentation.JokeUi

class FakeBaseRepository(
    private val manageResources: ManageResources

) : Repository<JokeUi, Error> {
    private var callback: ResultCallback<JokeUi, Error>? = null
    private val serviceError by lazy { Error.ServiceUnavailable(manageResources) }

    private var count = 0
    override fun fetch() {
        when (++count % 3) {
            0 -> callback?.provideSuccess(JokeUi.Base("test text$count", ""))
            1 -> callback?.provideSuccess(JokeUi.Favorite("favorite joke $count", ""))
            2 -> callback?.provideError(serviceError)
        }

    }

    override fun clear() {
        callback = null
    }

    override fun changeJokeStatus(resultCallback: ResultCallback<JokeUi, Error>) {
        // todo
    }

    override fun init(resultCallback: ResultCallback<JokeUi, Error>) {
        callback = resultCallback
    }
}