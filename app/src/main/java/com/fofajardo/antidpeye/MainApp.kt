package com.fofajardo.antidpeye

import android.app.Application
import com.google.android.material.color.DynamicColors

class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}
