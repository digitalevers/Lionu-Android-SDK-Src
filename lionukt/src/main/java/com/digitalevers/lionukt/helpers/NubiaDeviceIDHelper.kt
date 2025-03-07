package com.digitalevers.lionukt.helpers

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle

/****************************
 * on 2024/10/10
 * 努比亚 OAID
 */
class NubiaDeviceIDHelper(private val mConetxt: Context) {
    val nubiaID: String?
        get() {
            var oaid: String? = null
            val bundle: Bundle?

            val uri = Uri.parse("content://cn.nubia.identity/identity")
            try {
                if (Build.VERSION.SDK_INT > 17) {
                    val contentProviderClient =
                        mConetxt.contentResolver.acquireContentProviderClient(uri)
                    bundle = contentProviderClient!!.call("getOAID", null, null)
                    if (contentProviderClient != null) {
                        if (Build.VERSION.SDK_INT >= 24) {
                            contentProviderClient.close()
                        } else {
                            contentProviderClient.release()
                        }
                    }
                } else {
                    bundle = mConetxt.contentResolver.call(uri, "getOAID", null, null)
                }
                val code = bundle!!.getInt("code", -1)
                if (code == 0) {
                    oaid = bundle.getString("id")
                    return oaid
                }
                val faledMsg = bundle.getString("message")
                return oaid
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return oaid
        }
}
