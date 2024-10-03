package com.example.recipebook.application

import android.app.Application
import com.example.recipebook.Preference

class MyApplication:Application() {
    companion object {
         var preference: Preference?=null
    }
    override fun onCreate() {
        super.onCreate()
        preference= Preference(this)

    }
}