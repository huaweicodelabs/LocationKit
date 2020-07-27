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

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using Android.Util;

using Java.Lang;

namespace XLocationDemoProjectRef.Logger
{
    public class LocationLog
    {
        public static readonly int DEBUG = (int) LogPriority.Debug;

        public static readonly int INFO = (int) LogPriority.Info;

        public static readonly int WARN = (int) LogPriority.Warn;

        public static readonly int ERROR = (int) LogPriority.Error;

        private static ILogNode mLogNode;

        public static ILogNode GetLogNode() { return mLogNode; }

        public static void SetLogNode(ILogNode node) { mLogNode = node; }

        public static void Debug(string tag, string msg, Throwable tr)
        {
            WriteLine(DEBUG, tag, msg, tr);
        }

        public static void Debug(string tag, string msg)
        {
            Debug(tag, msg, null);
        }

        public static void Info(string tag, string msg, Throwable tr)
        {
            WriteLine(INFO, tag, msg, tr);
        }

        public static void Info(string tag, string msg)
        {
            Info(tag, msg, null);
        }

        public static void Warn(string tag, string msg, Throwable tr)
        {
            WriteLine(WARN, tag, msg, tr);
        }

        public static void Warn(string tag, string msg)
        {
            Warn(tag, msg, null);
        }

        public static void Warn(string tag, Throwable tr)
        {
            Warn(tag, null, tr);
        }

        public static void Error(string tag, string msg, Throwable tr)
        {
            WriteLine(ERROR, tag, msg, tr);
        }

        public static void Error(string tag, string msg)
        {
            Error(tag, msg, null);
        }

        public static void WriteLine(int priority, string tag, string msg, Throwable tr)
        {
            if (mLogNode != null)
            {
                mLogNode.WriteLine(priority, tag, msg, tr);
            }
        }

        public static void WriteLine(int priority, string tag, string msg)
        {
            WriteLine(priority, tag, msg, null);
        }
    }
}