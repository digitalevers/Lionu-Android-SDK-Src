package com.digitalevers.lionukt.helpers

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.Signature
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import com.digitalevers.lionukt.interfaces.OnePlusIDInterface
import java.security.MessageDigest

/****************************
 * * on 2024/10/10
 *
 */
class OnePlusDeviceIDHelper(private val mContext: Context) {
    var oaid: String = "OUID"
    private var sign: String? = null
    var onePlusIDInterface: OnePlusIDInterface? = null

    fun getID(_listener: DevicesIDsHelper.AppIdsUpdater?): String? {
        var res: String? = null

        check(Looper.myLooper() != Looper.getMainLooper()) { "Cannot run on MainThread" }

        val intent = Intent()
        intent.setComponent(ComponentName("com.heytap.openid", "com.heytap.openid.IdentifyService"))
        intent.setAction("action.com.heytap.openid.OPEN_ID_SERVICE")

        if (mContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)) {
            try {
                SystemClock.sleep(3000)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (onePlusIDInterface != null) {
                val oaid = realoGetIds("OUID")
                val support = isSupportOneplus

                res = oaid

                _listener?.OnIdsAvalid(oaid!!, support)
            }
        }
        return res
    }

    private fun realoGetIds(str: String): String? {
        var res: String? = null

        var str2: String? = null
        val pkgName = mContext.packageName
        if (sign == null) {
            var signatures: Array<Signature>?
            try {
                signatures = mContext.packageManager.getPackageInfo(pkgName, 64).signatures
            } catch (e: Exception) {
                e.printStackTrace()
                signatures = null
            }

            if (signatures != null && signatures.size > 0) {
                val byteArray = signatures[0].toByteArray()
                try {
                    val messageDigest = MessageDigest.getInstance("SHA1")
                    if (messageDigest != null) {
                        val digest = messageDigest.digest(byteArray)
                        val sb = StringBuilder()
                        for (b in digest) {
                            sb.append(
                                Integer.toHexString((b.toInt() and 255) or 256).substring(1, 3)
                            )
                        }
                        str2 = sb.toString()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            sign = str2
        }

        res = (onePlusIDInterface as OnePlusIDInterface.up.down).getSerID(pkgName, sign, str)
        return res
    }

    var serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            onePlusIDInterface = OnePlusIDInterface.up.genInterface(service)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            onePlusIDInterface = null
        }
    }


    private val isSupportOneplus: Boolean
        get() {
            var res = false

            try {
                val pm = mContext.packageManager
                val pNname = "com.heytap.openid"

                val pi = pm.getPackageInfo(pNname, 0)
                var ver: Long = 0
                ver = if (Build.VERSION.SDK_INT >= 28) {
                    pi!!.longVersionCode
                } else {
                    pi!!.versionCode.toLong()
                }
                if (pi != null && ver >= 1) {
                    res = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return res
        }
}
