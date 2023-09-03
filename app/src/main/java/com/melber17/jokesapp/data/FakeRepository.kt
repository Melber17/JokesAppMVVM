package com.melber17.jokesapp.data

import com.melber17.jokesapp.presentation.ManageResources
import com.melber17.jokesapp.presentation.JokeUi
import java.util.TimerTask

class FakeRepository(
    manageResources: ManageResources
): Repository<JokeUi, Error> {
    private val noConnection = Error.NoConnection(manageResources)
    private val serviceUnavailable = Error.ServiceUnavailable(manageResources)
    private var callback: ResultCallback<JokeUi, Error>? = null
    private var count = 1

    override fun fetch() {

     java.util.Timer().schedule(object: TimerTask() {
         override fun run() {
             if (count % 2 == 1 ) {
                 callback?.provideSuccess(JokeUi.Base("fake joke $count", "punchline"))
             } else if (count % 3 == 0) {
                 callback?.provideError(noConnection)
             } else {
                 callback?.provideError(serviceUnavailable)
             }
             count++
         }
     }, 2000)
    }

    override fun clear() {
       callback = null
    }

    override fun changeJokeStatus(resultCallback: ResultCallback<JokeUi, Error>) {
        // todo
    }

    override fun chooseFavorites(isFavorite: Boolean) {
        // todo
    }

    override fun init(resultCallback: ResultCallback<JokeUi, Error>) {
       callback = resultCallback
    }
}