package com.digitalevers.lionukt.util

import android.annotation.SuppressLint
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.digitalevers.lionukt.helpers.DevicesIDsHelper
import com.digitalevers.lionukt.helpers.DevicesIDsHelper.AppIdsUpdater

@SuppressLint("StaticFieldLeak")
class ApplicationProvider : ContentProvider() {

    private var mOAID: String? = null
    private var isSupported = false

    override fun onCreate(): Boolean {
        Log.d("digitalevers","create")
        val assetsData = context?.let { DeviceUtil.getAssetsData(it) }
        if (assetsData != null && assetsData.size >= 2) {
            sAppId = assetsData[0].toString()
            sHost = assetsData[1].toString()
            sPlanId = assetsData[2].toString()
            //判断shost中是否有协议字符
            if (sHost.contains("http://")) {
                sHost = sHost.replace("http://", "")
            } else if (sHost.contains("https://")) {
                sHost = sHost.replace("https://", "")
            }

            launch_url = HTTP + sHost + "/receive/launch"
            reg_url = HTTP + sHost + "/receive/reg"
            pay_url = HTTP + sHost + "/receive/pay"
        }
        try {
            val info = context!!.packageManager.getApplicationInfo(
                context!!.packageName, PackageManager.GET_META_DATA
            )
            if (info.metaData != null && info.metaData.containsKey("CHANNEL_KEY")) {
                sChannel = info.metaData.getString("CHANNEL_KEY", "")
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("TAG", "NameNotFoundException==> " + e.message)
        }

        val mDevicesIDsHelper: DevicesIDsHelper = DevicesIDsHelper( AppIdsUpdater{ ids, support ->
            if (ids == null || !support) {
                mOAID = ""
                isSupported = support
            } else {
                isSupported = support
                mOAID = ids
            }
            val preferences = context!!.getSharedPreferences("oaid", Context.MODE_PRIVATE)
            preferences?.edit()?.putString("OAID", mOAID)?.apply()
        })

        context?.let { mDevicesIDsHelper.getOAID(it) }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return 0
    }


    companion object {
        var context: Context? = null

        @JvmField
        var sAppId: String = ""
        var sHost: String = ""
        @JvmField
        var sChannel: String = ""
        @JvmField
        var sPlanId: String = ""

        @JvmField
        var launch_url: String = ""
        @JvmField
        var reg_url: String = ""
        @JvmField
        var pay_url: String = ""

        var HTTP: String = "https://" //Android Q 强制https协议
    }
}