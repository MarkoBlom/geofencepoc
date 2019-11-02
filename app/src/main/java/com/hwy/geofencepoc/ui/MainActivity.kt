package com.hwy.geofencepoc.ui

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.hwy.geofencepoc.R
import com.hwy.geofencepoc.extension.geofenceHelper
import com.hwy.geofencepoc.model.GeofenceModel

class MainActivity : AppCompatActivity() {

    companion object {
        private const val MY_LOCATION_PERMISSION_REQUEST_CODE = 543
    }

    private val mLocalBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            Log.d("=MB=","MainActivity::onReceive(...)")

            when (intent?.action) {
                "com.407.geofence.event" -> {
                    val info = intent.getStringExtra("DATA")
                    Toast.makeText(context, info, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    //
    private fun getLocalIntentFilter(): IntentFilter {

        val iFilter = IntentFilter()
        iFilter.addAction("com.407.geofence.event")
        return iFilter
    }

    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Check whether we've already granted a permission for location (which is a critical one)
        if ( ContextCompat.checkSelfPermission( this,
                                                Manifest.permission.ACCESS_FINE_LOCATION )
            != PackageManager.PERMISSION_GRANTED ) {

            Log.d("=MB=","MainActivity::onCreate() - NO LOC PERMISSION -> request for one...")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_LOCATION_PERMISSION_REQUEST_CODE
            )

        } else { // OK, we've already been granted permission
            Log.d("=MB=","MainActivity::onCreate() - WE ALREADY HAVE LOC PERMISSION")
            geofenceHelper().initGeofenceModels()
            geofenceHelper().startPollingEntries()
        }
    }


    /**
     * Permission request callback
     */
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        Log.d("=MB=","MainActivity::onRequestPermissionsResult(...)")
        if (requestCode == MY_LOCATION_PERMISSION_REQUEST_CODE) {
            checkPermissionResult()
        }
    }

    /**
     * Check if user granted a location permission
     */
    private fun checkPermissionResult() {

        if ( ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            Log.d("=MB=","  --> OK, we got our permission, move on..")
            geofenceHelper().initGeofenceModels()
            geofenceHelper().startPollingEntries()

        } else {

            Log.d("=MB=","  --> NO, no permission, quit..")
        }
    }

    override fun onStart() {
        super.onStart()

        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(mLocalBroadcastReceiver, getLocalIntentFilter())
    }

    override fun onStop() {
        super.onStop()

        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(mLocalBroadcastReceiver)
    }

    override fun onDestroy() {

        geofenceHelper().reset()

        super.onDestroy()
    }
}
