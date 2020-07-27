/*
*       Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Android;
using Android.App;
using Android.Content;
using Android.Content.PM;
using Android.Support.V4.App;
using Android.Util;
using Android.OS;

using XLocationDemoProjectRef.Logger;

namespace XLocationDemoProjectRef.Hmssample
{
    class RequestPermission
    {
        public static readonly string TAG = "RequestPermission";

        public static void RequestLocationPermission(Context context)
        {
            if (Build.VERSION.SdkInt <= BuildVersionCodes.P)
            {
                Log.Info(TAG, "Asking for location permission");
                if (ActivityCompat.CheckSelfPermission(context, Manifest.Permission.AccessFineLocation) == Permission.Denied
                    && ActivityCompat.CheckSelfPermission(context, Manifest.Permission.AccessCoarseLocation) == Permission.Denied)
                {
                    string[] permissions =
                            {Manifest.Permission.AccessFineLocation, Manifest.Permission.AccessCoarseLocation};
                    ActivityCompat.RequestPermissions((Activity)context, permissions, 1);
                }
            } else
            {
                if (ActivityCompat.CheckSelfPermission(context, Manifest.Permission.AccessFineLocation) == Permission.Denied
                    && ActivityCompat.CheckSelfPermission(context, Manifest.Permission.AccessCoarseLocation) == Permission.Denied
                    && ActivityCompat.CheckSelfPermission(context, "android.permission.ACCESS_BACKGROUND_LOCATION") != Permission.Denied)
                {
                    string[] permissions =
                            {Manifest.Permission.AccessFineLocation, Manifest.Permission.AccessCoarseLocation, "android.permission.ACCESS_BACKGROUND_LOCATION"};
                    ActivityCompat.RequestPermissions((Activity)context, permissions, 2);
                }
            }
        }

        public static void RequestActivityPermission(Context context)
        {
            if (Build.VERSION.SdkInt <= BuildVersionCodes.P)
            {
                if (ActivityCompat.CheckSelfPermission(context, "com.huawei.hms.permission.ACTIVITY_RECOGNITION") == Permission.Denied)
                {
                    string[] permissions = { "com.huawei.hms.permission.ACTIVITY_RECOGNITION" };
                    ActivityCompat.RequestPermissions((Activity)context, permissions, 1);
                    LocationLog.Info(TAG, "requestActivityTransitionButtonHandler: apply permission");
                }
            } 
            else
            {
                if (ActivityCompat.CheckSelfPermission(context, "android.permission.ACTIVITY_RECOGNITION") == Permission.Denied)
                {
                    string[] permissions = { "android.permission.ACTIVITY_RECOGNITION" };
                    ActivityCompat.RequestPermissions((Activity)context, permissions, 2);
                    LocationLog.Info(TAG, "requestActivityTransitionButtonHandler: apply permission");
                }
            }
        }
    }
}