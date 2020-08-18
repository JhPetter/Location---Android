package com.demo.testapplication.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.demo.testapplication.component.LocationLiveData

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private val locationData =
        LocationLiveData(application)

    fun getLocationData() = locationData
}