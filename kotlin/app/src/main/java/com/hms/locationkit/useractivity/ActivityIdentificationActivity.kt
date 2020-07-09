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
package com.hms.locationkit.useractivity

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import com.hms.locationkit.R
import com.hms.locationkit.activity.BaseActivity
import com.hms.locationkit.fusedlocation.LocationBroadcastReceiver
import com.hms.locationkit.logger.LocationLog
import com.hms.locationkit.utils.RequestPermission
import com.hms.locationkit.utils.Utils
import com.hms.locationkit.utils.Utils.ACTION_PROCESS_LOCATION
import com.hms.locationkit.utils.Utils.removeIdentificationListener
import com.huawei.hms.location.ActivityIdentification
import com.huawei.hms.location.ActivityIdentificationData
import com.huawei.hms.location.ActivityIdentificationService
import kotlinx.android.synthetic.main.activity_transition_type.*

class ActivityIdentificationActivity : BaseActivity(), View.OnClickListener {
    companion object {
        private var TAG = "ActivityTransitionUpdate"
        private const val ProgressBarOriginWidth = 100
        private const val Enlarge = 6
    }
    private var pendingIntent: PendingIntent? = null
    private lateinit var activityIdentificationService: ActivityIdentificationService
    private lateinit var type0: LayoutParams
    private lateinit var type1: LayoutParams
    private lateinit var type2: LayoutParams
    private lateinit var type3: LayoutParams
    private lateinit var type4: LayoutParams
    private lateinit var type5: LayoutParams
    private lateinit var type7: LayoutParams
    private lateinit var type8: LayoutParams
    private var activityInVehicle: LinearLayout? = null
    private var activityOnBicycle: LinearLayout? = null
    private var activityOnFoot: LinearLayout? = null
    private var activityStill: LinearLayout? = null
    private var activityUnknown: LinearLayout? = null
    private var activityTilting: LinearLayout? = null
    private var activityWalking: LinearLayout? = null
    private var activityRunning: LinearLayout? = null

    fun setData(list: List<ActivityIdentificationData>) {
        reSet()
        for (i in list.indices) {
            val type = list[i].identificationActivity
            val value = list[i].possibility
            try {
                when (type) {
                    ActivityIdentificationData.VEHICLE -> {
                        type0.width = type0.width + value * Enlarge
                        activityInVehicle?.layoutParams = type0
                    }
                    ActivityIdentificationData.BIKE -> {
                        type1.width = type1.width + value * Enlarge
                        activityOnBicycle?.layoutParams = type1
                    }
                    ActivityIdentificationData.FOOT -> {
                        type2.width = type2.width + value * Enlarge
                        activityOnFoot?.layoutParams = type2
                    }
                    ActivityIdentificationData.STILL -> {
                        type3.width = type3.width + value * Enlarge
                        activityStill?.layoutParams = type3
                    }
                    ActivityIdentificationData.OTHERS -> {
                        type4.width = type4.width + value * Enlarge
                        activityUnknown?.layoutParams = type4
                    }
                    ActivityIdentificationData.TILTING -> {
                        type5.width = type5.width + value * Enlarge
                        activityTilting?.layoutParams = type5
                    }
                    ActivityIdentificationData.WALKING -> {
                        type7.width = type7.width + value * Enlarge
                        activityWalking?.layoutParams = type7
                    }
                    ActivityIdentificationData.RUNNING -> {
                        type8.width = type8.width + value * Enlarge
                        activityRunning?.layoutParams = type8
                    }

                }
            } catch (e: Exception) {
                LocationLog.e("ActivityTransitionUpdate", "setdata Exception")
            }
        }
    }

    private fun reSet() {
        type0.width = ProgressBarOriginWidth
        activityInVehicle?.layoutParams = type0
        type1.width = ProgressBarOriginWidth
        activityOnBicycle?.layoutParams = type1
        type2.width = ProgressBarOriginWidth
        activityOnFoot?.layoutParams = type2
        type3.width = ProgressBarOriginWidth
        activityStill?.layoutParams = type3
        type4.width = ProgressBarOriginWidth
        activityUnknown?.layoutParams = type4
        type5.width = ProgressBarOriginWidth
        activityTilting?.layoutParams = type5
        type7.width = ProgressBarOriginWidth
        activityWalking?.layoutParams = type7
        type8.width = ProgressBarOriginWidth
        activityRunning?.layoutParams = type8
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transition_type)
        activityIdentificationService = ActivityIdentification.getService(this)
        RequestPermission.requestActivityTransitionPermission(this)
        requestActivityTransitionUpdate.setOnClickListener(this)
        removeActivityTransitionUpdate.setOnClickListener(this)
        type0 = activityInVehicle?.layoutParams as LayoutParams
        type1 = activityOnBicycle?.layoutParams as LayoutParams
        type2 = activityOnFoot?.layoutParams as LayoutParams
        type3 = activityStill?.layoutParams as LayoutParams
        type4 = activityUnknown?.layoutParams as LayoutParams
        type5 = activityTilting?.layoutParams as LayoutParams
        type7 = activityWalking?.layoutParams as LayoutParams
        type8 = activityRunning?.layoutParams as LayoutParams
        addLogFragment()
        reSet()
    }

    private fun requestActivityUpdates(detectionIntervalMillis: Long) {
        try {
            pendingIntent?.let {
                removeActivityUpdates()
            }

            pendingIntent = getPendingIntent()
            Utils.addIdentificationListener()
            activityIdentificationService.createActivityIdentificationUpdates(detectionIntervalMillis, pendingIntent)
                .addOnSuccessListener {
                    LocationLog.i(
                        TAG,
                        "createActivityIdentificationUpdates onSuccess"
                    )
                }
                .addOnFailureListener { e ->
                    LocationLog.e(
                        TAG,
                        "createActivityIdentificationUpdates onFailure:${e.message}"
                    )
                }
        } catch (e: Exception) {
            LocationLog.e(TAG, "createActivityIdentificationUpdates exception:${e.message}")
        }
    }

    private fun removeActivityUpdates() {
        reSet()
        try {
            removeIdentificationListener()
            Log.i(TAG, "start to removeActivityUpdates")
            activityIdentificationService.deleteActivityIdentificationUpdates(pendingIntent)
                .addOnSuccessListener {
                    LocationLog.i(
                        TAG,
                        "deleteActivityIdentificationUpdates onSuccess"
                    )
                }
                .addOnFailureListener { e ->
                    LocationLog.e(TAG, "deleteActivityIdentificationUpdates onFailure:${e.message}")
                }
        } catch (e: Exception) {
            LocationLog.e(TAG, "removeActivityUpdates exception:${e.message}")
        }
    }

    override fun onClick(v: View) {
        try {
            when (v.id) {
                R.id.requestActivityTransitionUpdate -> requestActivityUpdates(5000)
                R.id.removeActivityTransitionUpdate -> removeActivityUpdates()
            }
        } catch (e: Exception) {
            LocationLog.e(TAG, "RequestLocationUpdatesWithCallbackActivity Exception:$e")
        }
    }

    private fun getPendingIntent(): PendingIntent? {
        val intent = Intent(this, LocationBroadcastReceiver::class.java)
        intent.action = ACTION_PROCESS_LOCATION
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onDestroy() {
        if (pendingIntent != null) {
            removeActivityUpdates()
        }
        super.onDestroy()
    }
}