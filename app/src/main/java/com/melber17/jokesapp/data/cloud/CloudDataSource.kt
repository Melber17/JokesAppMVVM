package com.melber17.jokesapp.data.cloud

import com.melber17.jokesapp.presentation.ManageResources
import retrofit2.Call
import retrofit2.Response
import java.net.UnknownHostException

interface CloudDataSource {

    fun fetch(cloudCallback: JokeCloudCallback)

    class Base(
        private val jokeService: JokeService,
        private val manageResources: ManageResources
    ) : CloudDataSource {
        private val noConnection by lazy {
            com.melber17.jokesapp.data.Error.NoConnection(
                manageResources
            )
        }
        private val serviceError by lazy {
            com.melber17.jokesapp.data.Error.ServiceUnavailable(
                manageResources
            )
        }

        override fun fetch(cloudCallback: JokeCloudCallback) {
            jokeService.joke().enqueue(object : retrofit2.Callback<JokeCloud> {
                override fun onResponse(call: Call<JokeCloud>, response: Response<JokeCloud>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body == null) {
                            cloudCallback?.provideError(serviceError)
                        } else
                            cloudCallback?.provideJokeCloud(body)
                    } else
                        cloudCallback?.provideError(serviceError)
                }

                override fun onFailure(call: Call<JokeCloud>, t: Throwable) {
                    cloudCallback?.provideError(
                        if (t is UnknownHostException || t is java.net.ConnectException) {
                            noConnection
                        } else {
                            serviceError
                        }
                    )
                }

            })

        }
    }
}

interface JokeCloudCallback {
    fun provideJokeCloud(jokeCloud: JokeCloud)
    fun provideError(error: com.melber17.jokesapp.data.Error)
}
