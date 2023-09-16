package com.melber17.jokesapp.data.cloud

import com.melber17.jokesapp.data.cache.DataSource
import com.melber17.jokesapp.data.cache.JokeResult
import com.melber17.jokesapp.presentation.ManageResources
import java.lang.Exception
import java.net.UnknownHostException

interface CloudDataSource : DataSource {

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

        override suspend fun fetch(): JokeResult =
             try {
                val response = jokeService.joke().execute()
                JokeResult.Success(response.body()!!, false)
            } catch (error: Exception) {
                JokeResult.Failure(
                    if (error is UnknownHostException || error is java.net.ConnectException) {
                        noConnection
                    } else {
                        serviceError
                    }
                )
            }
        }
    }


