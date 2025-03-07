package com.digitalevers.lionukt.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Message

/****************************
 * on 2024/10/10
 */
class VivoDeviceIDHelper(private val mConetxt: Context) {
    private var handlerThread: HandlerThread? = null
    private var handler: Handler? = null
    private var isSupportIds = false
    var oaid1: String? = null

    @SuppressLint("Range")
    fun getOaid(): String? {
        var res: String? = null
        val uri = Uri.parse("content://com.vivo.vms.IdProvider/IdentifierId/OAID")
        val cursor = mConetxt.contentResolver.query(
            uri, null, null,
            null, null
        )
        if (cursor != null) {
            if (cursor.moveToNext()) {
                res = cursor.getString(cursor.getColumnIndex("value"))
            }
            cursor.close()
        } else {
        }
        return res
    }

    fun loge(): String? {
        val result: String? = null

        f()

        if (!isSupportIds) {
            return null
        }
        if (oaid1 != null) {
            return null
        }
        timeCheck(0, null)

        return null
    }

    private fun timeCheck(i: Int, str: String?) {
        val message = handler!!.obtainMessage()
        message.what = 11
        val bundle = Bundle()
        bundle.putInt("type", 0)
        if (i == 1 || i == 2) {
            bundle.putString("appid", str)
        }
        message.data = bundle
        handler!!.sendMessage(message)
    }

    private fun sysProperty(v1: String, v2: String): String? {
        var res: String? = null
        try {
            val clazz = Class.forName("android.os.SystemProperties")
            val method = clazz.getMethod(
                "get", *arrayOf<Class<*>>(
                    String::class.java, String::class.java
                )
            )

            res = method.invoke(clazz, *arrayOf<Any>(v1, v2)) as String
        } catch (e: Exception) {
            e.printStackTrace()
            return v2
        }
        return res
    }

    private fun isSupportIds(): Boolean {
        val isSupId = sysProperty("persist.sys.identifierid.supported", "0")
        isSupportIds = isSupId == "1"
        return isSupportIds
    }

    private fun f() {
        handlerThread = HandlerThread("SqlWorkThread")
        handlerThread!!.start()
        handler = object : Handler(handlerThread!!.looper) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                if (msg.what == 11) {
                    val tag = msg.data.getInt("type")
                    val name = msg.data.getString("appid")
                    val id = getContentResolver(tag, name)
                } else {
                }
            }
        }
    }

    @SuppressLint("Range")
    private fun getContentResolver(tag: Int, name: String?): String? {
        var result: String? = null
        val uri = when (tag) {
            0 -> Uri.parse("content://com.vivo.vms.IdProvider/IdentifierId/OAID")
            else -> null
        }
        val cursor = mConetxt.contentResolver.query(uri!!, null, null, null, null)
        if (cursor != null) {
            if (cursor.moveToNext()) {
                result = cursor.getString(cursor.getColumnIndex("value"))
            }
            cursor.close()
        } else {
        }
        return result
    }
}
