package com.digitalevers.lionukt.helpers

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.digitalevers.lionukt.interfaces.HWIDInterface
import java.util.concurrent.LinkedBlockingQueue

/****************************
 * *  * on 2024/10/10
 * 获取华为 OAID
 *
 */
class HWDeviceIDHelper(private val mContext: Context) {
    val linkedBlockingQueue: LinkedBlockingQueue<IBinder?> = LinkedBlockingQueue(1)

    fun getHWID(_listener: DevicesIDsHelper.AppIdsUpdater?) {
        try {
            mContext.packageManager.getPackageInfo("com.huawei.hwid", 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val bindIntent = Intent("com.uodis.opendevice.OPENIDS_SERVICE")
        bindIntent.setPackage("com.huawei.hwid")

        val isBin = mContext.bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        if (isBin) {
            try {
                val iBinder = linkedBlockingQueue.take()
                val hwID: HWIDInterface.HWID? = iBinder?.let { HWIDInterface.HWID(it, mContext) }
                val ids: String = hwID?.getIDs().toString()
                val boos: Boolean = hwID?.getBoos() == true
                val pps_oaid: String = hwID?.pPS_oaid.toString()
                val support = isSupport

                _listener?.OnIdsAvalid("$ids\npps_oadi: $pps_oaid", support)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                mContext.unbindService(serviceConnection)
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
                val pm = mContext.packageManager
                pm.getPackageInfo("com.huawei.hwid", 0)
                val intent = Intent("com.uodis.opendevice.OPENIDS_SERVICE")
                intent.setPackage("com.huawei.hwid")
                return !pm.queryIntentServices(intent, 0).isEmpty()
            } catch (e: Exception) {
                return false
            }
        }
}
