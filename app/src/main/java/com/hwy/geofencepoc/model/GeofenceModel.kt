package com.hwy.geofencepoc.model

import com.google.android.gms.location.Geofence

/**
 * Presents a single geofence point
 * lat
 * lon
 * desc
 */
data class GeofenceModel(val lat: Double,
                    val lon: Double,
                    val radius: Float,
                    val id: String,
                    val transitionType: Int,
                    val desc: String = "") {
}