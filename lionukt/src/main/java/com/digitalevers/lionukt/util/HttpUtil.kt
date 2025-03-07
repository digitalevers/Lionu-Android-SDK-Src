package com.digitalevers.lionukt.util

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.Log
import com.digitalevers.lionukt.util.DeviceUtil.getAndroidId
import com.digitalevers.lionukt.util.DeviceUtil.getAppName
import com.digitalevers.lionukt.util.DeviceUtil.getDeviceId
import com.digitalevers.lionukt.util.DeviceUtil.getIPAddress
import com.digitalevers.lionukt.util.DeviceUtil.getMac
import com.digitalevers.lionukt.util.DeviceUtil.getOaid
import com.digitalevers.lionukt.util.DeviceUtil.getVersionCode
import com.digitalevers.lionukt.util.DeviceUtil.getVersionName
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal object HttpUtil {
    private val FAIL = 0
    private val SUCCESS = 1
    private val ERROR = 2

    private val handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                SUCCESS -> {}
                FAIL -> {}
                ERROR -> {}
                else -> {}
            }
        }
    }

    @JvmStatic
    fun postUsage(context: Context?, uri: String, amount: Float) {
        if (context == null) {
            return
        }
        try {
            val params = JSONObject()
            params.put("appid", ApplicationProvider.sAppId)
            params.put("planid", ApplicationProvider.sPlanId)
            params.put("os", "1")
            if (!TextUtils.isEmpty(ApplicationProvider.sChannel)) {
                params.put("channel", ApplicationProvider.sChannel)
            }
            if (amount > 0) {
                params.put("amount", amount.toString())
            }
            val deviceInfo = JSONObject()

            deviceInfo.put("applicationId", context.packageName)
            deviceInfo.put("appName", getAppName(context))
            deviceInfo.put("versionCode", getVersionCode(context).toString())
            deviceInfo.put("versionName", getVersionName(context))
            deviceInfo.put("time", yyyyMMddHHmmss())

            deviceInfo.put("imei", getDeviceId(context)) //md5 哈希值
            deviceInfo.put("androidid", getAndroidId(context)) //md5 哈希值
            deviceInfo.put("oaid", getOaid(context)) //md5 哈希值
            deviceInfo.put("mac", getMac(context)) //md5 哈希值
            deviceInfo.put("model", Build.MODEL)
            deviceInfo.put("sys", Build.VERSION.SDK_INT.toString())
            deviceInfo.put("ua", System.getProperty("http.agent"))
            deviceInfo.put("ip", getIPAddress(context)) //md5 哈希值

            params.put("deviceInfo", deviceInfo)

            if (RP.debug) {
                Log.i("RP===>", params.toString())
            }
            post(uri, params.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * post请求基础方法
     * @param uri
     * @param body
     */
    private fun post(uri: String, body: String) {
        Thread(Runnable {
            try {
                val url = URL(uri)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.doInput = true
                connection.useCaches = false
                connection.setRequestProperty("Content-Type", "application/json;charset=utf-8")
                connection.connect()
                val writer = BufferedWriter(
                    OutputStreamWriter(
                        connection.outputStream,
                        StandardCharsets.UTF_8
                    )
                )
                writer.write(body)
                writer.close()
                val responseCode = connection.responseCode
                if (RP.debug) {
                    Log.i("RP===>", responseCode.toString())
                }
                if (responseCode == 200) {
                    /**
                     * 如果获取的code为200，则证明数据获取是正确的。
                     */
                    /**
                     * 如果获取的code为200，则证明数据获取是正确的。
                     */
                    /**
                     * 如果获取的code为200，则证明数据获取是正确的。
                     */
                    /**
                     * 如果获取的code为200，则证明数据获取是正确的。
                     */
                    /**
                     * 如果获取的code为200，则证明数据获取是正确的。
                     */
                    /**
                     * 如果获取的code为200，则证明数据获取是正确的。
                     */
                    /**
                     * 如果获取的code为200，则证明数据获取是正确的。
                     */

                    /**
                     * 如果获取的code为200，则证明数据获取是正确的。
                     */
                    val `is` = connection.inputStream
                    val result = readMyInputStream(`is`)
                    /**
                     * 子线程发送消息到主线程，并将获取的结果带到主线程，让主线程来更新UI。
                     */
                    /**
                     * 子线程发送消息到主线程，并将获取的结果带到主线程，让主线程来更新UI。
                     */
                    /**
                     * 子线程发送消息到主线程，并将获取的结果带到主线程，让主线程来更新UI。
                     */
                    /**
                     * 子线程发送消息到主线程，并将获取的结果带到主线程，让主线程来更新UI。
                     */
                    /**
                     * 子线程发送消息到主线程，并将获取的结果带到主线程，让主线程来更新UI。
                     */
                    /**
                     * 子线程发送消息到主线程，并将获取的结果带到主线程，让主线程来更新UI。
                     */
                    /**
                     * 子线程发送消息到主线程，并将获取的结果带到主线程，让主线程来更新UI。
                     */

                    /**
                     * 子线程发送消息到主线程，并将获取的结果带到主线程，让主线程来更新UI。
                     */
                    val msg = Message()
                    msg.obj = result
                    msg.what = SUCCESS
                    handler.sendMessage(msg)
                } else {
                    val msg = Message()
                    msg.what = FAIL
                    handler.sendMessage(msg)
                }
                connection.disconnect()
            } catch (e: Exception) {
                Log.e("post exception", (e.message)!!)
                /**
                 * 如果获取失败，或出现异常，那么子线程发送失败的消息（FAILURE）到主线程，主线程显示Toast，提示发送请求失败
                 */
                /**
                 * 如果获取失败，或出现异常，那么子线程发送失败的消息（FAILURE）到主线程，主线程显示Toast，提示发送请求失败
                 */
                /**
                 * 如果获取失败，或出现异常，那么子线程发送失败的消息（FAILURE）到主线程，主线程显示Toast，提示发送请求失败
                 */
                /**
                 * 如果获取失败，或出现异常，那么子线程发送失败的消息（FAILURE）到主线程，主线程显示Toast，提示发送请求失败
                 */
                /**
                 * 如果获取失败，或出现异常，那么子线程发送失败的消息（FAILURE）到主线程，主线程显示Toast，提示发送请求失败
                 */
                /**
                 * 如果获取失败，或出现异常，那么子线程发送失败的消息（FAILURE）到主线程，主线程显示Toast，提示发送请求失败
                 */
                /**
                 * 如果获取失败，或出现异常，那么子线程发送失败的消息（FAILURE）到主线程，主线程显示Toast，提示发送请求失败
                 */

                /**
                 * 如果获取失败，或出现异常，那么子线程发送失败的消息（FAILURE）到主线程，主线程显示Toast，提示发送请求失败
                 */
                val msg = Message()
                msg.what = ERROR
                handler.sendMessage(msg)
            }
        }).start()
    }

    private fun yyyyMMddHHmmss(): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return format.format(Date())
    }

    /**
     * 读取字节流转为字符串
     * @param is
     * @return
     */
    fun readMyInputStream(`is`: InputStream): String {
        val result: ByteArray
        try {
            val baos = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var len: Int
            while ((`is`.read(buffer).also { len = it }) != -1) {
                baos.write(buffer, 0, len)
            }
            `is`.close()
            baos.close()
            result = baos.toByteArray()
        } catch (e: IOException) {
            e.printStackTrace()
            val errorStr = "获取数据失败。"
            return errorStr
        }
        return String(result)
    }
}
