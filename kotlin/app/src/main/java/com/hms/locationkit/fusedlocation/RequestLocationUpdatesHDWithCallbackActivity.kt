/**
 * Copyright 2020 Huawei Technologies co, Ltd All
 * Rights reserved
 * Licenced under the Apache License,Version 2.0(the "License");
 * you may not use this file except in compliance with license
 * you may obtain a copy of the license at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by application law or agreed to in writing software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permission and
 * limitations under the License
 */
package com.hms.locationkit.fusedlocation

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.hms.locationkit.R
import com.hms.locationkit.activity.BaseActivity
import com.hms.locationkit.logger.LocationLog
import com.hms.locationkit.utils.JsonDataUtil
import com.huawei.hms.location.*
import kotlinx.android.synthetic.main.activity_hms_hd.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

class RequestLocationUpdatesHDWithCallbackActivity : BaseActivity(), View.OnClickListener {
    companion object {
        private const val TAG = "RequestLocationHD"
    }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var mLocationHDCallback: LocationCallback? = null

    private fun initDataDisplayView(tableLayout: TableLayout, json: String) {
        try {
            val jsonObject = JSONObject(json)
            val iterator: Iterator<*> = jsonObject.keys()
            while (iterator.hasNext()) {
                val key = iterator.next() as String
                val value = jsonObject.getString(key)
                val textView = TextView(baseContext).apply {
                    text = key
                    setTextColor(Color.GRAY)
                    id = baseContext.resources.getIdentifier(
                        "$key _key",
                        "id",
                        baseContext.packageName
                    )
                }

                val editText = EditText(baseContext).apply {
                    setText(value)
                    id = baseContext.resources.getIdentifier(
                        "$key _value",
                        "id",
                        baseContext.packageName
                    )
                    setTextColor(Color.DKGRAY)

                }
                val tableRow = TableRow(baseContext).apply {
                    addView(textView)
                    addView(editText)
                }
                tableLayout.addView(tableRow)
            }
        } catch (e: JSONException) {
            Log.e(TAG, "initDataDisplayView JSONException:${e.message}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hms_hd)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val tableLayout: TableLayout = callback_table_layout_show
        val locationRequestJson: String =
            JsonDataUtil.getJson(this, "LocationRequest.json", true)
        initDataDisplayView(tableLayout, locationRequestJson)
        btn_remove_hd.setOnClickListener(this)
        btn_hd.setOnClickListener(this)
        addLogFragment()
        if (ActivityCompat.checkSelfPermission(
                this@RequestLocationUpdatesHDWithCallbackActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@RequestLocationUpdatesHDWithCallbackActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val strings = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            ActivityCompat.requestPermissions(this, strings, 1)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_hd -> getLocationWithHd()
            R.id.btn_remove_hd -> removeLocationHd()
        }
    }

    private fun removeLocationHd() {
        GlobalScope.launch {
            try {
                fusedLocationProviderClient.removeLocationUpdates(mLocationHDCallback)
                    .addOnSuccessListener { LocationLog.i(TAG, "removeLocationHd onSuccess") }
                    .addOnFailureListener { e ->
                        LocationLog.i(TAG, "removeLocationHd onFailure:${e.message}")
                    }
            } catch (e: Exception) {
                LocationLog.e(TAG, "removeLocationHd exception:${e.message}")
            }
        }
        Log.i(TAG, "call removeLocationUpdatesWithCallback success.")
    }

    private fun getLocationWithHd() {

        GlobalScope.launch {
            try {
                val locationRequest = LocationRequest()
                if (null == mLocationHDCallback) {
                    mLocationHDCallback = object : LocationCallback() {
                        override fun onLocationResult(locationRequest: LocationResult) {
                            Log.i(TAG, "getLocationWithHd callback onLocationResult print")
                            logResult(locationRequest)
                        }

                        override fun onLocationAvailability(locationAvailability: LocationAvailability?) {
                            Log.i(TAG, "getLocationWithHd callback onLocationAvailability print")
                            locationAvailability?.let {
                                val flag = locationAvailability.isLocationAvailable
                                LocationLog.i(
                                    TAG,
                                    "onLocationAvailability isLocationAvailable:$flag"
                                )
                            }

                        }
                    }
                }
                fusedLocationProviderClient.requestLocationUpdatesEx(
                    locationRequest, mLocationHDCallback,
                    Looper.getMainLooper()
                ).addOnSuccessListener { LocationLog.i(TAG, "getLocationWithHd onSuccess") }
                    .addOnFailureListener { e ->
                        LocationLog.i(TAG, "getLocationWithHd onFailure:${e.message}")
                    }
            } catch (e: Exception) {
                LocationLog.i(TAG, "getLocationWithHd exception :${e.message}")
            }

        }
    }

    private fun logResult(locationRequest: LocationResult?) {
        locationRequest?.let {
            Log.i(TAG, "getLocationWithHd callback  onLocationResult locationResult is not null")
            val locations = locationRequest.getLocations()
            var hdFlag: String
            if (locations.isNotEmpty()) {
                Log.i(TAG, "getLocationWithHd callback  onLocationResult location is not empty")
                for (location in locations) {
                    hdFlag = if (location.accuracy < 2) {
                        "result is HD"
                    } else {
                        ""
                    }
                    LocationLog.i(
                        TAG,
                        """[old]location result :Longitude = ${location.longitude}Latitude = ${location.latitude}Accuracy = ${location.accuracy}$hdFlag""".trimIndent()
                    )
                }
            }
            val hwLocations =
                locationRequest.hwLocationList
            if (hwLocations.isNotEmpty()) {
                Log.i(TAG, "getLocationWithHd callback  onLocationResult location is not empty")
                for (hwLocation in hwLocations) {

                    if (hwLocation.countryName.isEmpty()) {
                        return
                    }
                    hdFlag = if (hwLocation.accuracy < 2) {
                        "result is HD"
                    } else {
                        ""
                    }
                    LocationLog.i(
                        TAG,
                        """[new]location result :Longitude = ${hwLocation.longitude}Latitude = ${hwLocation.latitude}Accuracy = ${hwLocation.accuracy}${hwLocation.countryName},${hwLocation.state},${hwLocation.city},${hwLocation.county},${hwLocation.featureName}$hdFlag""".trimIndent()
                    )
                }
            }
        }
    }
}
