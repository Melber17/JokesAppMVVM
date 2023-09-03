package com.melber17.jokesapp.data

import androidx.annotation.StringRes
import com.melber17.jokesapp.presentation.ManageResources
import com.melber17.jokesapp.R


interface Error {
    fun message(): String

    abstract class Abstract(
        private val manageResources: ManageResources,
        @StringRes private val messageId: Int
    ) : Error {
        override fun message(): String {
            return manageResources.string(messageId)
        }
    }

    class NoConnection(private val manageResources: ManageResources) :
        Abstract(manageResources, R.string.no_connection_message)

    class ServiceUnavailable(private val manageResources: ManageResources) :
        Abstract(manageResources, R.string.service_unavailable_message)

    class NoFavoriteJoke(private val manageResources: ManageResources) :
        Abstract(manageResources, R.string.no_favorite_joke)
}