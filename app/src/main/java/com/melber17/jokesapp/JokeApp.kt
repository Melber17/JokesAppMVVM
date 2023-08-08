package com.melber17.jokesapp

import android.app.Application

class JokeApp: Application() {
    private lateinit var viewModel: MainViewModel
    override fun onCreate() {
        super.onCreate()
        viewModel = MainViewModel(FakeModel())

    }
}