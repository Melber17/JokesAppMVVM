package com.melber17.jokesapp

class FakeModel: Model<Any, Any> {

    private var callback: ResultCallback<Any, Any>? = null
    override fun fetch() {
        TODO("Not yet implemented")
    }

    override fun clear() {
       callback = null
    }

    override fun init(resultCallback: ResultCallback<Any, Any>) {
       callback = resultCallback
    }
}