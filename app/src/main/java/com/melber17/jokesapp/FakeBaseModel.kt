package com.melber17.jokesapp

class FakeBaseModel(
    private val manageResources: ManageResources

) : Model<Joke, Error> {
    private var callback: ResultCallback<Joke, Error>? = null
    private val serviceError by lazy { Error.ServiceUnavailable(manageResources) }

    private var count = 0
    override fun fetch() {
        when (++count % 3) {
            0 -> callback?.provideSuccess(Joke.Base("test text$count", ""))
            1 -> callback?.provideSuccess(Joke.Favorite("favorite joke $count", ""))
            2 -> callback?.provideError(serviceError)
        }

    }

    override fun clear() {
        callback = null
    }

    override fun init(resultCallback: ResultCallback<Joke, Error>) {
        callback = resultCallback
    }
}