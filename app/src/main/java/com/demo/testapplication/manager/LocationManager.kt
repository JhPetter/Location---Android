package com.demo.testapplication.manager

import android.content.Context
import android.location.LocationManager
import androidx.activity.ComponentActivity
import com.demo.testapplication.component.LocationLiveData
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes

typealias OnLocationStatusListener = (LocationStatus) -> Unit

class LocationManager(private val activity: ComponentActivity) {
    private val locationManager =
        activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val locationSettingsRequest: LocationSettingsRequest?

    init {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(LocationLiveData.locationRequest)
        locationSettingsRequest = builder.build()
        builder.setAlwaysShow(true)
    }

    fun isGPSEnable(): Boolean = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    fun enableGPS(onLocationStatusListener: OnLocationStatusListener) {
        if (isGPSEnable())
            onLocationStatusListener(LocationStatus.SUCCESS)
        else
            LocationServices.getSettingsClient(activity)
                .checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener {
                    onLocationStatusListener(LocationStatus.SUCCESS)
                }
                .addOnFailureListener { e ->
                    when ((e as ApiException).statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            if (e is ResolvableApiException)
                                showDialogEnableGPS(e)
                            onLocationStatusListener(LocationStatus.ASKING_PERMISSION)
                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE ->
                            onLocationStatusListener(LocationStatus.ERROR)
                    }

                }
    }

    private fun showDialogEnableGPS(resolvableApiException: ResolvableApiException) {
        resolvableApiException.startResolutionForResult(activity, 0)
    }
}

enum class LocationStatus() {
    SUCCESS, ERROR, ASKING_PERMISSION
}