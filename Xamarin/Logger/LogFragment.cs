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
using Android.Text;

using XLocationDemoProjectRef.Hmssample;
using Java.Lang;

namespace XLocationDemoProjectRef.Logger
{
    public class LogFragment : Fragment
    {
        private LogView mLogView;

        private ScrollView mScrollView;

        public LogFragment()
        {

        }

        private View InflateViews()
        {
            mScrollView = new ScrollView(this.Activity);

            mScrollView.LayoutParameters = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MatchParent, ViewGroup.LayoutParams.MatchParent);

            mLogView = new LogView(Activity);
            mLogView.Clickable = true;

            mScrollView.AddView(mLogView,
                 new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MatchParent, ViewGroup.LayoutParams.WrapContent));

            if (Constant.IsLog == 0)
            {
                mScrollView.Visibility = ViewStates.Gone;
            }

            return mScrollView;
        }

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View result = InflateViews();

            mLogView.AddTextChangedListener(new TextWatcherImpl(mScrollView));

            /**
            * double click on the TextView,the application will clean the info window
            */
            GestureDetector gestureDetector = new GestureDetector(new GestureDoubleTapImpl(mLogView));

            mLogView.SetOnTouchListener(new OnTouchListenerImpl(gestureDetector));

            return result;
        }

        public LogView GetLogView()
        {
            return mLogView;
        }
    }

    class TextWatcherImpl : Java.Lang.Object, ITextWatcher
    {
        private ScrollView mScrollView;
        public TextWatcherImpl(ScrollView scrollView)
        {
            mScrollView = scrollView;
        }

        public void AfterTextChanged(IEditable s)
        {
            mScrollView.Post(() =>
            {
                mScrollView.FullScroll(FocusSearchDirection.Down);
            });
        }

        public void BeforeTextChanged(ICharSequence s, int start, int count, int after)
        {
        }

        public void OnTextChanged(ICharSequence s, int start, int before, int count)
        {
        }
    }

    class GestureDoubleTapImpl : GestureDetector.SimpleOnGestureListener
    {
        LogView mLogView;

        public GestureDoubleTapImpl(LogView logView)
        {
            mLogView = logView;
        }

        public override bool OnDoubleTap(MotionEvent e)
        {
            mLogView.SetText("", TextView.BufferType.Normal);
            return true;
        }
    }

    class OnTouchListenerImpl : Java.Lang.Object, View.IOnTouchListener
    {
        private GestureDetector gestureDetector;

        public OnTouchListenerImpl (GestureDetector gestureDetector) {
            this.gestureDetector = gestureDetector;
        }

        public bool OnTouch(View v, MotionEvent e)
        {
            return gestureDetector.OnTouchEvent(e);
        }
    }
}