package com.digitalevers.lionukt.helpers

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log

/****************************
 * on 2024/10/10
 */
class MeizuDeviceIDHelper(private val mContext: Context) {
    val isMeizuSupport: Boolean
        get() {
            try {
                val pm = mContext.packageManager
                if (pm != null) {
                    val pi = pm.resolveContentProvider(
                        "com.meizu.flyme.openidsdk",
                        0
                    ) // "com.meizu.flyme.openidsdk"
                    if (pi != null) {
                        return true
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }

    fun getMeizuID(_listener: DevicesIDsHelper.AppIdsUpdater?) {
        try {
            mContext.packageManager.getPackageInfo("com.meizu.flyme.openidsdk", 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val uri = Uri.parse("content://com.meizu.flyme.openidsdk/")
        val cursor: Cursor
        val contentResolver = mContext.contentResolver
        try {
            cursor = contentResolver.query(uri, null, null, arrayOf("oaid"), null)!!
            val oaid = getOaid(cursor)
            val support = isMeizuSupport


            _listener?.OnIdsAvalid(oaid!!, support)
            cursor.close()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    /**
     * 获取 OAID
     *
     * @param cursor
     * @return
     */
    private fun getOaid(cursor: Cursor?): String? {
        var oaid: String? = null
        if (cursor == null || cursor.isClosed) {
            Log.d("MEIZU :", "oaid null")
            return null
        }


        cursor.moveToFirst()
        var valueIdx = cursor.getColumnIndex("value")
        if (valueIdx > 0) {
            oaid = cursor.getString(valueIdx)
        }
        valueIdx = cursor.getColumnIndex("code")
        if (valueIdx > 0) {
            val codeID = cursor.getInt(valueIdx)
        }
        valueIdx = cursor.getColumnIndex("expired")
        if (valueIdx > 0) {
            val timeC = cursor.getLong(valueIdx)
        }

        return oaid
    }
}
