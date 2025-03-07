package com.android.reportx.util;

import android.content.Context;

public class RP {

    public static boolean debug = false;

    public static void launch(Context context) {
        HttpUtil.postUsage(context, ApplicationProvider.launch_url, 0);
    }

    public static void reg(Context context) {
        HttpUtil.postUsage(context, ApplicationProvider.reg_url, 0);
    }

    public static void pay(Context context, float amount) {
        HttpUtil.postUsage(context, ApplicationProvider.pay_url, amount);
    }
}
