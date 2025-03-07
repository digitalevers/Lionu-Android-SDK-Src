package com.digitalevers.lionukt.helpers

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.digitalevers.lionukt.interfaces.ZTEIDInterface
import java.util.concurrent.LinkedBlockingQueue

class ZTEDeviceIDHelper(var mContext: Context) {
    var idPkgName: String = "com.mdid.msa"

    private fun checkService(): Int {
        var s = 0
        try {
            mContext.packageManager.getPackageInfo(idPkgName, 0)
            s = 1
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return s
    }

    private fun startMsaklServer(pkgName: String) {
        if (checkService() > 0) {   // 这里等于虚设
            //
        }
        val intent = Intent()
        intent.setClassName(idPkgName, "com.mdid.msa.service.MsaKlService")
        intent.setAction("com.bun.msa.action.start.service")
        intent.putExtra("com.bun.msa.param.pkgname", pkgName)
        try {
            intent.putExtra("com.bun.msa.param.runinset", true)
            if (mContext.startService(intent) != null) {
                return
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getID(_listener: DevicesIDsHelper.AppIdsUpdater?) {
        try {
            mContext.packageManager.getPackageInfo(idPkgName, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val pkgName = mContext.packageName
        startMsaklServer(pkgName)

        val v0 = Intent()
        v0.setClassName("com.mdid.msa", "com.mdid.msa.service.MsaIdService")
        v0.setAction("com.bun.msa.action.bindto.service")
        v0.putExtra("com.bun.msa.param.pkgname", pkgName)
        val isBin = mContext.bindService(v0, serviceConnection, Context.BIND_AUTO_CREATE)
        if (isBin) {
            try {
                val iBinder = linkedBlockingQueue.take()
                val zteidInterface: ZTEIDInterface = ZTEIDInterface.up.down(iBinder!!)
                val oaid: String = zteidInterface.getOAID().toString()
                val support: Boolean = zteidInterface.isSupported()
                _listener?.OnIdsAvalid(oaid, support)

                mContext.unbindService(serviceConnection)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                mContext.unbindService(serviceConnection)
            }
        }
    }

    val linkedBlockingQueue: LinkedBlockingQueue<IBinder> = LinkedBlockingQueue(1)
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
