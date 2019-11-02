package com.hwy.geofencepoc.util

import android.annotation.SuppressLint
import android.app.IntentService
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.hwy.geofencepoc.PocApp

class GeofenceTransitionsIntentService : IntentService("GeoTransitionIntentService") {

    @SuppressLint("ByteOrderMark")
    override fun onHandleIntent(intent: Intent?) {

        Log.d("=MB=", "GeofenceTransitionsIntentService::onHandleIntent(...)")

        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent.hasError()) {
//            val errorMessage = GeofenceErrorMessages.getErrorString(
//                this,
//                geofencingEvent.errorCode
//            )
            Log.e("=MB=", "GeofenceTransitionsIntentService() error: " + geofencingEvent.errorCode) // errorMessage
            val intent = Intent()
            intent.action = "com.407.geofence.event"
            intent.putExtra("DATA", "GeofenceTransitionsIntentService() error:" + geofencingEvent.errorCode)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            return
        }

        // No errors, success:
        handleEvent(geofencingEvent)
    }

    private fun handleEvent(event: GeofencingEvent) {

        val entry = (event.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)

        // Get the geofences that were triggered. A single event can trigger
        // multiple geofences.
        val triggeringGeofences = event.triggeringGeofences

        // how many ? we should get just one
        for( geofence in triggeringGeofences) {
            Log.d("=MB=", "GeofenceTransitionsIntentService() ID: " + geofence.requestId);
            val desc = (application as PocApp).getGeofenceHelper().getGeofenceDescription(geofence.requestId, entry)
            if( desc != null ) {
                Log.d("=MB=", "Geofence description: " + desc);
                val intent = Intent()
                intent.action = "com.407.geofence.event"
                intent.putExtra("DATA", "Geofence description: " + desc)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            }
        }
    }
}