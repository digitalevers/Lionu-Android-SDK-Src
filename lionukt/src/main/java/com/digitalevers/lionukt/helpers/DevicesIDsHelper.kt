package com.digitalevers.lionukt.helpers

import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.Log
import java.util.Locale

/****************************
 * on 2024/10/10
 ***************************/
class DevicesIDsHelper(private val _listener: AppIdsUpdater) {
    private val brand: String get() = Build.BRAND.uppercase(Locale.getDefault())

    private val manufacturer: String get() = Build.MANUFACTURER.uppercase(Locale.getDefault())

    fun getOAID(mcontext: Context) {
        var oaid: String? = null
        var isSupport = false

        Log.d("OAID_TOOL", "getManufacturer ===> " + manufacturer)

        val manufacturer = manufacturer.uppercase(Locale.getDefault())

        if ("ASUS" == manufacturer) {
            getIDFromNewThead(mcontext, manufacturer)
        } else if ("HUAWEI" == manufacturer) {
            getIDFromNewThead(mcontext, manufacturer)
        } else if ("LENOVO" == manufacturer) {
            LenovoDeviceIDHelper(mcontext).getIdRun(_listener)
        } else if ("MOTOLORA" == manufacturer) {
            LenovoDeviceIDHelper(mcontext).getIdRun(_listener)
        } else if ("MEIZU" == manufacturer) {
            MeizuDeviceIDHelper(mcontext).getMeizuID(_listener)
        } else if ("NUBIA" == manufacturer) {
            oaid = NubiaDeviceIDHelper(mcontext).nubiaID
        } else if ("OPPO" == manufacturer) {
            getIDFromNewThead(mcontext, manufacturer)
        } else if ("SAMSUNG" == manufacturer) {
            SamsungDeviceIDHelper(mcontext).getSumsungID(_listener)
        } else if ("VIVO" == manufacturer) {
            oaid = VivoDeviceIDHelper(mcontext).getOaid()
        } else if ("XIAOMI" == manufacturer) {
            oaid = XiaomiDeviceIDHelper(mcontext).oAID
        } else if ("BLACKSHARK" == manufacturer) {
            oaid = XiaomiDeviceIDHelper(mcontext).oAID
        } else if ("ONEPLUS" == manufacturer) {
            getIDFromNewThead(mcontext, manufacturer)
        } else if ("ZTE" == manufacturer) {
            getIDFromNewThead(mcontext, manufacturer)
        } else if ("FERRMEOS" == manufacturer || isFreeMeOS) {
            getIDFromNewThead(mcontext, manufacturer)
        } else if ("SSUI" == manufacturer || isSSUIOS) {
            getIDFromNewThead(mcontext, manufacturer)
        }

        if (oaid != null) {
            isSupport = true
        }

        _listener.OnIdsAvalid(oaid, isSupport)
    }

    private fun getProperty(property: String?): String? {
        var res: String? = null
        if (property == null) {
            return null
        }
        try {
            val clazz = Class.forName("android.os.SystemProperties")
            val method = clazz.getMethod(
                "get", *arrayOf<Class<*>>(
                    String::class.java,
                    String::class.java
                )
            )
            res = method.invoke(clazz, *arrayOf<Any>(property, "unknown")) as String
        } catch (e: Exception) {
            // ignore
        }
        return res
    }


    val isFreeMeOS: Boolean
        get() {
            val pro = getProperty("ro.build.freeme.label") // "ro.build.freeme.label"
            if ((!TextUtils.isEmpty(pro)) && pro.equals(
                    "FREEMEOS",
                    ignoreCase = true
                )
            ) {      // "FreemeOS"  FREEMEOS
                return true
            }
            return false
        }

    val isSSUIOS: Boolean
        get() {
            val pro = getProperty("ro.ssui.product") // "ro.ssui.product"
            if ((!TextUtils.isEmpty(pro)) && (!pro.equals("unknown", ignoreCase = true))) {
                return true
            }
            return false
        }


    /**
     * 启动子线程获取
     */
    private fun getIDFromNewThead(contextm: Context, manufacturerm: String) {
        Thread(object : Runnable {
            override fun run() {
                if ("ASUS" == manufacturerm) {
                    ASUSDeviceIDHelper(contextm).getID(_listener)
                } else if ("HUAWEI" == manufacturerm) {
                    HWDeviceIDHelper(contextm).getHWID(_listener)
                } else if ("OPPO" == manufacturerm) {
                    OppoDeviceIDHelper(contextm).getID(_listener)
                } else if ("ONEPLUS" == manufacturerm) {
                    OnePlusDeviceIDHelper(contextm).getID(_listener)
                } else if ("ZTE" == manufacturerm) {
                    ZTEDeviceIDHelper(contextm).getID(_listener)
                } else if ("FERRMEOS" == manufacturerm || isFreeMeOS) {
                    ZTEDeviceIDHelper(contextm).getID(_listener)
                } else if ("SSUI" == manufacturerm || isSSUIOS) {
                    ZTEDeviceIDHelper(contextm).getID(_listener)
                }
            }
        }).start()
    }

    //函数式接口
    fun interface AppIdsUpdater {
        fun OnIdsAvalid(ids: String?, support: Boolean)
    }
}