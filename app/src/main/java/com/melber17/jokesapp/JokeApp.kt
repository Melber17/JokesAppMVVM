package com.melber17.jokesapp

import android.app.Application

class JokeApp: Application() {
    lateinit var viewModel: MainViewModel
    override fun onCreate() {
        super.onCreate()
        ManageResources.Base(this)
        viewModel = MainViewModel(FakeModel(ManageResources.Base(applicationContext)))

    }
}