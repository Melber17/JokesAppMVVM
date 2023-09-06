package com.melber17.jokesapp

import android.app.Application
import com.melber17.jokesapp.data.BaseRepository
import com.melber17.jokesapp.data.cache.CacheDataSource
import com.melber17.jokesapp.data.cache.ProvideRealm
import com.melber17.jokesapp.data.cloud.CloudDataSource
import com.melber17.jokesapp.data.cloud.JokeService
import com.melber17.jokesapp.presentation.MainViewModel
import com.melber17.jokesapp.presentation.ManageResources
import io.realm.Realm
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class JokeApp : Application() {

    lateinit var viewModel: MainViewModel
    override fun onCreate() {
        super.onCreate()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://official-joke-api.appspot.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        Realm.init(this)
        viewModel = MainViewModel(
            BaseRepository(
                CloudDataSource.Base(
                    retrofit.create(JokeService::class.java),
                    ManageResources.Base(this)
                ),
                CacheDataSource.Base(
                    object: ProvideRealm {
                        override fun provideRealm(): Realm {
                            return Realm.getDefaultInstance()
                        }
                    },
                    ManageResources.Base(this)
                ),
            )
        )

    }

}