package com.digitalevers.lionukt.helpers

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.digitalevers.lionukt.interfaces.ASUSIDInterface
import java.util.concurrent.LinkedBlockingQueue

/****************************
 * on 2024/10/10
 * 华硕手机获取 OAID
 ***************************/
class ASUSDeviceIDHelper(private val mContext: Context) {
    val linkedBlockingQueue: LinkedBlockingQueue<IBinder> = LinkedBlockingQueue<IBinder>(1)

    /**
     * 获取 OAID 并回调
     *
     * @param _listener
     */
    fun getID(_listener: DevicesIDsHelper.AppIdsUpdater?) {
        try {
            mContext.packageManager.getPackageInfo("com.asus.msa.SupplementaryDID", 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val intent = Intent()

        intent.setAction("com.asus.msa.action.ACCESS_DID")
        val componentName = ComponentName(
            "com.asus.msa.SupplementaryDID",
            "com.asus.msa.SupplementaryDID.SupplementaryDIDService"
        )
        intent.setComponent(componentName)


        val isBin = mContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        if (isBin) {
            try {
                val iBinder = linkedBlockingQueue.take()
                val asusID: ASUSIDInterface.ASUSID = ASUSIDInterface.ASUSID(iBinder)
                val asusOAID: String = asusID.getID().toString()
                val support: Boolean = asusID.isSupport()

                if (_listener != null) {
                    _listener.OnIdsAvalid(asusOAID, support)
                }
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
}