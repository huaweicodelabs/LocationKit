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

package com.huawei.logger;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * LogView is used to show LocationLog info
 *
 * @since 2020-5-11
 */
public class LogView extends TextView implements LogNode {
    private LogNode mNext;

    public LogView(Context context) {
        super(context);
    }

    public LogView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LogView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LogNode getNext() {
        return mNext;
    }

    public void setNext(LogNode node) {
        mNext = node;
    }

    @Override
    public void println(int priority, String tag, String msg, Throwable tr) {
        String priorityStr = null;
        switch (priority) {
            case LocationLog.DEBUG:
                priorityStr = "D";
                break;
            case LocationLog.INFO:
                priorityStr = "I";
                break;
            case LocationLog.WARN:
                priorityStr = "W";
                break;
            case LocationLog.ERROR:
                priorityStr = "E";
                break;
            default:
                break;
        }
        String exceptionStr = null;
        if (tr != null) {
            exceptionStr = android.util.Log.getStackTraceString(tr);
        }
        final StringBuilder outputBuilder = new StringBuilder();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        outputBuilder.append(str);
        outputBuilder.append(" ");
        outputBuilder.append(msg);
        outputBuilder.append((char) 13).append((char) 10);
        if (getContext() instanceof Activity) {
            Activity activity = (Activity) getContext();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    appendToLog(outputBuilder.toString());
                }
            });
        }
        if (mNext != null) {
            mNext.println(priority, tag, msg, tr);
        }
    }

    public void appendToLog(String s) {
        append("\n" + s);
    }
}
