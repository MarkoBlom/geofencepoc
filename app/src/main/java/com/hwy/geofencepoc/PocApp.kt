package com.hwy.geofencepoc

import android.app.Application
import com.hwy.geofencepoc.util.GeofenceHelper

class PocApp : Application() {

    private lateinit var helper: GeofenceHelper

    override fun onCreate() {
        super.onCreate()

        helper = GeofenceHelper(this)
    }

    fun getGeofenceHelper() = helper
}