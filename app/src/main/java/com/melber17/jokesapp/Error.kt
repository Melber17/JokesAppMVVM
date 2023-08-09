package com.melber17.jokesapp

import androidx.annotation.StringRes


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
}