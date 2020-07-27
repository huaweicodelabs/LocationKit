using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Util;
using Android.Views;
using Android.Widget;
using Com.Huawei.Agconnect.Config;

namespace XLocationDemoProjectRef 
{
    class HmsLazyInputStream : LazyInputStream
    {
        public HmsLazyInputStream(Context context) : base(context)
        {

        }
        public override Stream Get(Context context)
        {
            try
            {
                return context.Assets.Open("agconnect-services.json");
            }
            catch (Exception e)
            {
                Log.Error(e.ToString(), "Can't open agconnect file");
                return null;
            }
        }
    }
}