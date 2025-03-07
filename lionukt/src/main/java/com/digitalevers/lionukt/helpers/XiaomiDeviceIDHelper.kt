package com.digitalevers.lionukt.helpers

import android.content.Context
import java.lang.reflect.Method

/****************************
 * on 2024/10/10
 */
class XiaomiDeviceIDHelper(private val mContext: Context) {
    private var idProvider: Class<*>? = null
    private var idImpl: Any? = null
    private var oaid: Method? = null


    init {
        try {
            idProvider = Class.forName("com.android.id.impl.IdProviderImpl")
            idImpl = idProvider?.newInstance()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            oaid = idProvider!!.getMethod("getOAID", *arrayOf<Class<*>>(Context::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun invokeMethod(ctx: Context, method: Method?): String? {
        var result: String? = null
        if (idImpl != null && method != null) {
            try {
                result = method.invoke(idImpl, ctx) as String
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return result
    }

    val oAID: String?
        get() = invokeMethod(mContext, oaid)
}
