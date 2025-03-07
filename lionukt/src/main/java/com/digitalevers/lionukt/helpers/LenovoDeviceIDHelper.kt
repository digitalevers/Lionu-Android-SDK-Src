package com.digitalevers.lionukt.helpers

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.digitalevers.lionukt.interfaces.LenovoIDInterface

/****************************
 * on 2024/10/10
 * 获取联想 OAID
 *
 */
class LenovoDeviceIDHelper(private val mContext: Context) {
    var lenovoIDInterface: LenovoIDInterface? = null

    fun getIdRun(_listener: DevicesIDsHelper.AppIdsUpdater?) {
        val intent = Intent()
        intent.setClassName("com.zui.deviceidservice", "com.zui.deviceidservice.DeviceidService")
        val seu = mContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        if (seu) {
            if (lenovoIDInterface != null) {
                val oaid: String = lenovoIDInterface?.a().toString()
                val support: Boolean = lenovoIDInterface!!.c()

                _listener?.OnIdsAvalid(oaid, support)
            }
        }
    }

    var serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            lenovoIDInterface = LenovoIDInterface.len_up.len_down(service)
        }

        override fun onServiceDisconnected(name: ComponentName) {
        }
    }
}
