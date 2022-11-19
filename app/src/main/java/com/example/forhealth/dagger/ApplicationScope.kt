package com.example.forhealth.dagger

import android.app.Application
import javax.inject.Singleton

@Singleton
class ApplicationScope: Application() {

    lateinit var myComponent: MyComponent

    override fun onCreate() {
        super.onCreate()
        myComponent = DaggerMyComponent.factory().create(applicationContext)
    }
}