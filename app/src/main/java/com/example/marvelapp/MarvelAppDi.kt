package com.example.marvelapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MarvelAppDi : Application() {

    val teste = "teste"
    //val teste2 = "teste2"
}