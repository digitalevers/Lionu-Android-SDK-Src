package com.digitalevers.lionukt.helpers

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.digitalevers.lionukt.interfaces.SamsungIDInterface
import java.util.concurrent.LinkedBlockingQueue

/****************************
 * * on 2024/10/10
 */
class SamsungDeviceIDHelper(private val mContext: Context) {
    val linkedBlockingQueue: LinkedBlockingQueue<IBinder> = LinkedBlockingQueue(1)

    fun getSumsungID(_listener: DevicesIDsHelper.AppIdsUpdater?) {
        try {
            mContext.packageManager.getPackageInfo("com.samsung.android.deviceidservice", 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }


        val intent = Intent()
        intent.setClassName(
            "com.samsung.android.deviceidservice",
            "com.samsung.android.deviceidservice.DeviceIdService"
        )
        val isBinded = mContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        if (isBinded) {
            try {
                val iBinder = linkedBlockingQueue.take()
                val proxy: SamsungIDInterface.Proxy = SamsungIDInterface.Proxy(iBinder) // 在这里有区别，需要实际验证

                val oaid: String = proxy.getID().toString()
                val support = isSupport

                _listener?.OnIdsAvalid(oaid, support)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    var serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            try {
                linkedBlockingQueue.put(service)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
        }
    }

    val isSupport: Boolean
        get() {
            try {
                val pi =
                    mContext.packageManager.getPackageInfo("com.samsung.android.deviceidservice", 0)
                return pi != null
            } catch (e: Exception) {
                return false
            }
        }
}
