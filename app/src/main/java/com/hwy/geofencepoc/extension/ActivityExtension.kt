package com.hwy.geofencepoc.extension

import android.app.Activity
import com.hwy.geofencepoc.PocApp

/**
 * Get GeofenceHelper from Activity
 */
fun Activity.geofenceHelper() = (application as PocApp).getGeofenceHelper()