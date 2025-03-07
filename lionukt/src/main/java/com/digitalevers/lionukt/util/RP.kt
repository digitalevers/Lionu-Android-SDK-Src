package com.digitalevers.lionukt.util

import android.content.Context
import com.digitalevers.lionukt.util.HttpUtil.postUsage

object RP {
    var debug: Boolean = false

    fun launch(context: Context?) {
        postUsage(context, ApplicationProvider.launch_url, 0f)
    }

    fun reg(context: Context?) {
        postUsage(context, ApplicationProvider.reg_url, 0f)
    }

    fun pay(context: Context?, amount: Float) {
        postUsage(context, ApplicationProvider.pay_url, amount)
    }
}
