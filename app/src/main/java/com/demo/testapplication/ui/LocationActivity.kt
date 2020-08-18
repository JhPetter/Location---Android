package com.demo.testapplication.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.demo.testapplication.R
import com.demo.testapplication.databinding.ActivityLocationBinding
import com.demo.testapplication.manager.LocationManager
import com.demo.testapplication.manager.LocationStatus
import com.demo.testapplication.manager.PermissionManager
import com.demo.testapplication.util.showDialog

class LocationActivity : AppCompatActivity() {

    private val locationViewModel: LocationViewModel by lazy {
        ViewModelProviders.of(this).get(LocationViewModel::class.java)
    }

    private val permissionManager: PermissionManager by lazy {
        PermissionManager(this, onDeny = {
            openRationaleLocationPermission(locationManger.isGPSEnable())
        }, onRationale = {
            openRationaleLocationPermission(true)
        })
    }
    private val locationManger: LocationManager by lazy { LocationManager(this) }
    private lateinit var binding: ActivityLocationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.loadLocation.setOnClickListener {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        permissionManager.requestPermission(
            permission = Manifest.permission.ACCESS_FINE_LOCATION,
            onGranted = {
                locationManger.enableGPS {
                    when (it) {
                        LocationStatus.SUCCESS -> {
                            loadLocation()
                        }
                        LocationStatus.ASKING_PERMISSION -> {
                            binding.latLong.text = "Press again"
                        }
                        LocationStatus.ERROR -> {
                            binding.latLong.text = "Error"
                        }
                    }
                }
            })
    }

    private fun loadLocation() {
        locationViewModel.getLocationData().observe(this, Observer {
            binding.latLong.text = getString(R.string.latLong, it.longitude, it.latitude)
        })
    }

    private fun openRationaleLocationPermission(isRational: Boolean = false) {
        this.showDialog(R.layout.component_dialog, false) { dialog ->
            dialog.mView.findViewById<ImageView>(R.id.dialog_icon)
                .setImageResource(R.drawable.ic_surprise)
            dialog.mView.findViewById<TextView>(R.id.dialog_text_title).text =
                getString(R.string.location_not_active)
            dialog.mView.findViewById<TextView>(R.id.dialog_text_description).text =
                getString(R.string.location_text_active)

            dialog.mView.findViewById<AppCompatButton>(R.id.dialog_btn_accept).text =
                getString(R.string.location_active)

            dialog.mView.findViewById<AppCompatButton>(R.id.dialog_btn_accept).setOnClickListener {
                dialog.dismiss()
                openPermissionSettings(isRational)
            }

            dialog.mView.findViewById<View>(R.id.dialog_close)
                .setOnClickListener {
                    dialog.dismiss()
                }
        }
    }

    private fun openPermissionSettings(isRational: Boolean) {
        if (!isRational)
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        else {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }
    }
}