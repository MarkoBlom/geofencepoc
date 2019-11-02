package com.hwy.geofencepoc.util

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.hwy.geofencepoc.model.GeofenceModel

class GeofenceHelper(private val context: Context) {

    // active geofences i.e. to be added to geofence request
    private var mGeofenceList : MutableList<Geofence> = mutableListOf<Geofence>()

    private val mGeofencingClient = LocationServices.getGeofencingClient(context)

    // we have two different types of geofences, entry and exit
    // entry geofences
    private lateinit var mEntrySet : Set<GeofenceModel>

    // exit geofences
    private lateinit var mExitSet : Set<GeofenceModel>

    private val mGeofencePendingIntent: PendingIntent by lazy {

        val intent = Intent(context, GeofenceTransitionsIntentService::class.java)

        PendingIntent.getService(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT)
    }


    fun startPollingEntries() {

        if ( ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            buildGeofenceList(mEntrySet)

            mGeofencingClient
                .addGeofences( buildGeofencingRequest(), mGeofencePendingIntent)
                .addOnSuccessListener {
                    Log.d("=MB=", "GeofenceHelper::startPollingEntries() : SUCCESS !!!")
                }
                .addOnFailureListener {
                    Log.e("=MB=", "GeofenceHelper::startPollingEntries() : ERROR !!!")
                }
        }
    }

    fun startPollingExits() {

        if ( ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            buildGeofenceList(mExitSet)

            mGeofencingClient
                .addGeofences( buildGeofencingRequest(), mGeofencePendingIntent)
                .addOnSuccessListener {
                    Log.d("=MB=", "GeofenceHelper::startPollingExits() : SUCCESS !!!")
                }
                .addOnFailureListener {
                    Log.e("=MB=", "GeofenceHelper::startPollingEntries() : ERROR !!!")
                }
        }
    }

    fun getGeofenceDescription( id: String, entry: Boolean) : String? {

        if(entry)
            for( geofence in mEntrySet )
                if(geofence.id == id)
                    return geofence.desc
        else
            for( geofence in mExitSet )
                if(geofence.id == id)
                    return geofence.desc

        return null;
    }

    fun reset() {

        mGeofencingClient?.removeGeofences(mGeofencePendingIntent)?.run {
            addOnSuccessListener {
                // Geofences removed
                Log.d("=MB=", "GeofenceHelper::clear() SUCCESS !");
            }
            addOnFailureListener {
                // Failed to remove geofences
                Log.e("=MB=", "GeofenceHelper::clear() FAILURE !");
            }
        }

        mGeofenceList?.clear()
    }

    /**
     *
     */
    private fun buildGeofenceList( modelList: Set<GeofenceModel> ) {

        for ( model in modelList ) {
            mGeofenceList.add( parseToGeofence(model) )
        }

        Log.d("=MB=", "GeofenceHelper::buildGeofenceList() : Num of Geofences = " + mGeofenceList.size)
    }
    //
    /**
     *
     */
    private fun parseToGeofence(geofence: GeofenceModel): Geofence {

         return Geofence.Builder()
            // uniquely identifies the geofence
            .setRequestId(geofence.id)

            .setCircularRegion(
                geofence.lat,
                geofence.lon,
                geofence.radius
            )
            // To trigger an event when the user enter/exit/dwell the geofence
            .setTransitionTypes(geofence.transitionType)

            // this geofence will exist until the user removes it
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()
    }


    /**
     * init hardcoded geofence values for detecting entries to and exits from hwy
     */
    fun initGeofenceModels() {

        /*  You can create a list of geofence objects by setting the latitude, longitude,
             radius, duration, and transition types of each geofence.
             The transition types indicate the events that trigger the geofence,
             such as when users enter or exit a geofence.
        */

        // Note:    On single-user devices, there is a limit of 100 geofences per app.
        //          For multi-user devices, the limit is 100 geofences per app per device user.

        // Entry points:
        // https://alvinalexander.com/kotlin/kotlin-functions-to-create-lists-maps-sets
        mEntrySet = setOf(
            GeofenceModel(33.013191, -97.076368, 150.0f,"TEST_0", Geofence.GEOFENCE_TRANSITION_ENTER,"home"),
            GeofenceModel(33.013646, -97.071097, 150.0f,"ENTRY_1", Geofence.GEOFENCE_TRANSITION_ENTER,"from home, Flower Mound Rd."),
            GeofenceModel(33.023965, -97.071029, 100.0f,"ENTRY_2", Geofence.GEOFENCE_TRANSITION_ENTER,"from Callaway"),
            GeofenceModel(33.036039, -97.071038, 150.0f,"ENTRY_3", Geofence.GEOFENCE_TRANSITION_ENTER,"from Kroger, Cross Timber"))


        // Exit points:
        mExitSet = setOf(
            GeofenceModel(33.010458, -97.071089, 150.0f,"EXIT_1", Geofence.GEOFENCE_TRANSITION_EXIT,"North Shore Blvd. to home"),
            GeofenceModel(33.013534, -97.070893, 150.0f,"EXIT_2", Geofence.GEOFENCE_TRANSITION_EXIT,"Flower Mound Rd. to Panda Express"),
            GeofenceModel(33.025498, -97.071024, 150.0f,"EXIT_3", Geofence.GEOFENCE_TRANSITION_EXIT,"to Black Walnut Cafe"),
            GeofenceModel(33.035824, -97.069831, 150.0f,"EXIT_4", Geofence.GEOFENCE_TRANSITION_EXIT,"Cross Timbers, to Lewisville"),
            GeofenceModel(33.037887, -97.069948, 150.0f,"EXIT_5", Geofence.GEOFENCE_TRANSITION_EXIT,"to Texas Roadhouse"))
    }


    /**
     *
     */
    private fun buildGeofencingRequest(): GeofencingRequest {

//        return GeofencingRequest.Builder()
//            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
//            .addGeofences(mGeofenceList)
//            .build()

        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(mGeofenceList)
        }.build()
    }
}