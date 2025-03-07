package com.digitalevers.lionukt.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaDrm
import android.media.UnsupportedSchemeException
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import com.digitalevers.lionukt.util.CheckPermission.verifyStoragePermissions
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException
import java.lang.Math.pow
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.nio.charset.StandardCharsets
import java.util.Collections
import java.util.Enumeration
import java.util.Locale
import java.util.UUID
import kotlin.math.pow

object DeviceUtil {
    private val TAG: String = DeviceUtil::class.java.name

    private var sWifiMac: String?
    private var sImei: String
    private var sMeid: String?

    init {
        sWifiMac = ""
        sImei = ""
        sMeid = ""
    }


    /*public static String getImei(Context context) {
        if (!TextUtils.isEmpty(sImei)) {
            return sImei;
        } else {
            String deviceId = null;
            try {
                if (context != null) {
                    TelephonyManager manager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
                    if (manager != null && checkPermission(context, android.Manifest.permission.READ_PHONE_STATE)) {
                        deviceId = manager.getDeviceId();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            sImei = deviceId;
            return deviceId;
        }
    }*/
    @JvmStatic
    fun getAndroidId(context: Context?): String? {
        var androidId: String? = null
        if (context != null) {
            try {
                androidId = Settings.Secure.getString(context.contentResolver, "android_id")
            } catch (e: Exception) {
                Log.e(TAG, e.message!!)
            }
        }
        return androidId
    }

    /**
     * 获取 Android 设备序列号
     * @return
     */
    fun getSerial(context: Context?): String? {
        if (Build.VERSION.SDK_INT >= 9) {
            var serial: String? = ""
            val sdkInt = Build.VERSION.SDK_INT
            if (sdkInt >= 26) {
                try {
                    val clazz = Class.forName("android.os.Build")
                    val method = clazz.getMethod("getSerial")
                    serial = method.invoke(clazz) as String
                } catch (throwable: Throwable) {
                    /*if(throwable.getMessage() != null) {
                        Log.e(TAG, throwable.getMessage());
                    }*/
                    println(throwable.message)
                }
            } else {
                serial = Build.SERIAL
            }
            return serial
        } else {
            return ""
        }
    }

    private fun getSerialNo(context: Context): String? {
        var serialNo: String? = ""
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                val clazz = Class.forName("android.os.Build")
                val method = clazz.getMethod("getSerial")
                serialNo = method.invoke(clazz) as String
            } catch (throwable: Throwable) {
                Log.e(TAG, throwable.message!!)
            }
        } else {
            serialNo = Build.SERIAL
        }
        return serialNo
    }


    private fun checkPermission(context: Context?, permissions: String): Boolean {
        var isGranted = false
        if (context == null) {
            return isGranted
        } else {
            if (Build.VERSION.SDK_INT >= 23) {
                try {
                    val clazz = Class.forName("android.content.Context")
                    val method = clazz.getMethod("checkSelfPermission", String::class.java)
                    val result = method.invoke(context, permissions) as Int
                    isGranted = if (result == 0) {
                        true
                    } else {
                        false
                    }
                } catch (throwable: Throwable) {
                    isGranted = false
                    Log.e(TAG, throwable.message!!)
                }
            } else {
                val packageManager = context.packageManager
                if (packageManager.checkPermission(
                        permissions,
                        context.packageName
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    isGranted = true
                }
            }
            return isGranted
        }
    }


    private val macByJavaAPI: String?
        get() {
            try {
                val enumeration: Enumeration<*> = NetworkInterface.getNetworkInterfaces()
                var networkInterface: NetworkInterface
                do {
                    if (!enumeration.hasMoreElements()) {
                        return null
                    }
                    networkInterface = enumeration.nextElement() as NetworkInterface
                } while ("wlan0" != networkInterface.name && "eth0" != networkInterface.name)

                val hardwareAddress = networkInterface.hardwareAddress
                if (hardwareAddress != null && hardwareAddress.size != 0) {
                    val builder = StringBuilder()
                    val address = hardwareAddress
                    val length = hardwareAddress.size

                    for (i in 0 until length) {
                        val b = address[i]
                        builder.append(String.format("%02X:", b))
                    }
                    if (builder.length > 0) {
                        builder.deleteCharAt(builder.length - 1)
                    }
                    return builder.toString().lowercase(Locale.getDefault())
                }
                return null
            } catch (throwable: Throwable) {
                Log.e(TAG, throwable.message!!)
            }
            return null
        }

    private val macShell: String?
        get() {
            val addressStr = arrayOf(
                "/sys/class/net/wlan0/address",
                "/sys/class/net/eth0/address",
                "/sys/devices/virtual/net/wlan0/address"
            )
            try {
                for (i in addressStr.indices) {
                    try {
                        val reaAddress = reaMac(addressStr[i])
                        if (reaAddress != null) {
                            return reaAddress
                        }
                    } catch (throwable: Throwable) {
                        Log.e(TAG, throwable.message!!)
                    }
                }
            } catch (throwable: Throwable) {
                Log.e(TAG, throwable.message!!)
            }
            return null
        }

    private fun reaMac(s: String): String? {
        var mac: String? = null
        try {
            val fileReader = FileReader(s)
            var bufferedReader: BufferedReader? = null
            if (fileReader != null) {
                try {
                    bufferedReader = BufferedReader(fileReader, 1024)
                    mac = bufferedReader.readLine()
                } finally {
                    if (fileReader != null) {
                        try {
                            fileReader.close()
                        } catch (throwable: Throwable) {
                            Log.e(TAG, throwable.message!!)
                        }
                    }
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close()
                        } catch (throwable: Throwable) {
                            Log.e(TAG, throwable.message!!)
                        }
                    }
                }
            }
        } catch (throwable: Throwable) {
            Log.e(TAG, throwable.message!!)
        }
        return mac
    }


    fun getMeid(context: Context?): String? {
        var meid: String? = null
        if (context == null) {
            return null
        } else {
            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (telephonyManager == null) {
                return null
            } else {
                try {
                    if (checkPermission(
                            context,
                            Manifest.permission.READ_PHONE_STATE
                        ) && telephonyManager != null
                    ) {
                        if (Build.VERSION.SDK_INT < 26) {
                            meid = getIMEI(context)
                        } else {
                            try {
                                meid = meid(context)
                                if (TextUtils.isEmpty(meid)) {
                                    meid = getIMEI(context)
                                }
                            } catch (throwable: Throwable) {
                                Log.e(TAG, throwable.message!!)
                            }
                        }
                    }
                } catch (throwable: Throwable) {
                    Log.e(TAG, throwable.message!!)
                }
            }
            return meid
        }
    }

    private fun meid(context: Context?): String? {
        if (TextUtils.isEmpty(sMeid)) {
            return sMeid
        } else {
            var meid: String? = null
            if (context == null) {
                return null
            } else {
                try {
                    val clazz = Class.forName("android.telephony.TelephonyManager")
                    val method = clazz.getMethod("getMeid")
                    val obj = method.invoke(null as Any?)
                    if (null != obj && obj is String) {
                        meid = obj
                    }
                } catch (throwable: Throwable) {
                    Log.e(TAG, throwable.message!!)
                }
                sMeid = meid
                return sMeid
            }
        }
    }

    /**
     * 获取局域网ip地质
     * @param context
     * @return
     */
    @JvmStatic
    fun getIPAddress(context: Context?): String? {
        var ipv4Host: String? = null
        var ipv6Host: String? = null
        try {
            val enumeration: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            val iterator: Iterator<*> = Collections.list(enumeration).iterator()

            while (iterator.hasNext()) {
                val networkInterface = iterator.next() as NetworkInterface
                val inetAddresses: Enumeration<InetAddress> = networkInterface.inetAddresses
                val addressIterator: Iterator<*> = Collections.list(inetAddresses).iterator()

                while (addressIterator.hasNext()) {
                    val inetAddress = addressIterator.next() as InetAddress
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address && !networkInterface.displayName.contains(
                            "dummy"
                        ) && ipv4Host == null
                    ) {
                        ipv4Host = inetAddress.getHostAddress()
                    }

                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet6Address && !networkInterface.displayName.contains(
                            "dummy"
                        ) && ipv6Host == null
                    ) {
                        ipv6Host = inetAddress.getHostAddress()
                    }
                }
            }
        } catch (socketException: SocketException) {
            ipv4Host = "SocketException"
            Log.e(TAG, socketException.message!!)
        }
        if (ipv4Host != null) {
            return ipv4Host
        } else {
            ipv4Host = ipv6Host
            if (ipv6Host == null) {
                ipv4Host = "null"
            }
            return ipv4Host
        }
    }


    /**
     * 获取mac地址
     * @param context
     * @return
     */
    @JvmStatic
    fun getMac(context: Context?): String? {
        if (!TextUtils.isEmpty(sWifiMac)) {
            return sWifiMac
        } else {
            var mac: String? = ""
            if (context == null) {
                return mac
            }
            if (Build.VERSION.SDK_INT < 23) {
                mac = getMacBySystemInterface(context)
            } else if (Build.VERSION.SDK_INT == 23) {
                mac = macByJavaAPI
                if (TextUtils.isEmpty(mac)) {
                    mac = macShell
                }
            } else {
                mac = macByJavaAPI
                if (TextUtils.isEmpty(mac)) {
                    mac = getMacBySystemInterface(context)
                }
            }
            sWifiMac = mac
            return sWifiMac
        }
    }

    private fun getMacBySystemInterface(context: Context?): String {
        if (context == null) {
            return ""
        } else {
            try {
                val manager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                if (checkPermission(context, Manifest.permission.ACCESS_WIFI_STATE)) {
                    if (manager != null) {
                        val wifiInfo = manager.connectionInfo
                        return wifiInfo.macAddress
                    } else {
                        return ""
                    }
                } else {
                    return ""
                }
            } catch (throwable: Throwable) {
                Log.e(TAG, throwable.message!!)
                return ""
            }
        }
    }


    fun getPackageName(context: Context?): String? {
        return context?.packageName
    }

    /**
     * 获取IMEI封装方法
     * API低于29 直接获取
     * API高于29 模拟生成一个IMEI
     * @param context
     * @return md5哈希值
     */
    @JvmStatic
    fun getDeviceId(context: Context?): String? {
        var deviceId = ""
        if (context != null) {
            try {
                val preferences = context.getSharedPreferences("GUID", Context.MODE_PRIVATE)
                deviceId = preferences.getString("imei", "").toString()
                if (TextUtils.isEmpty(deviceId)) {
                    deviceId = readExternal("imei.txt", context)
                    if (TextUtils.isEmpty(deviceId)) {
                        deviceId = if (Build.VERSION.SDK_INT < 29) {
                            getIMEI(context)
                        } else {
                            randomNum(15).toString() + "m"
                        }
                        //write deviceId to SD Card
                        writeExternal("imei.txt", deviceId, context)
                    }
                    preferences.edit().putString("imei", deviceId).apply()
                }

                /*if (TextUtils.isEmpty(deviceId)) {
                    deviceId = getSerialNo();
                }*/
            } catch (throwable: Throwable) {
                Log.e(TAG, throwable.message!!)
            }
            /*if (TextUtils.isEmpty(deviceId)) {
                deviceId = getGUID(context);
            }*/
        }
        return deviceId
    }


    val widevineID: String
        /**
         * 获取数字版权管理设备ID
         *
         * @return WidevineID，可能为空
         */
        get() {
            try {
                //See https://stackoverflow.com/questions/16369818/how-to-get-crypto-scheme-uuid
                //You can find some UUIDs in the https://github.com/google/ExoPlayer source code
                val WIDEVINE_UUID = UUID(-0x121074568629b532L, -0x5c37d8232ae2de13L)
                val mediaDrm = MediaDrm(WIDEVINE_UUID)
                val widevineId = mediaDrm.getPropertyByteArray(MediaDrm.PROPERTY_DEVICE_UNIQUE_ID)
                    ?: return ""
                val sb = StringBuilder()
                for (aByte in widevineId) {
                    sb.append(String.format("%02x", aByte))
                }

                return sb.toString()
            } catch (e: UnsupportedSchemeException) {
                Log.e(TAG, e.toString())
            }
            return ""
        }

    /**
     * 模拟 factor 位数的随即数字串
     * @param factor
     * @return
     */
    private fun randomNum(factor: Int): Double {
        return (Math.random() + 1) * 10.0.pow((factor - 1).toDouble())
    }

    /**
     * 获取 Android 设备唯一码 uuid
     * @param context
     * @return
     */
    fun getGUID(context: Context): String? {
        val preferences = context.getSharedPreferences("GUID", Context.MODE_PRIVATE)
        var uuid = preferences.getString("uuid", "")
        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString()
            preferences.edit().putString("uuid", uuid).apply()
        }
        return uuid
    }

    //ApplicationProvider初始化的时候会实例化DevicesIDsHelper 并将 lambda表达式传给函数式接口
    //多线程中获得oaid后回调该 lambda 表达式，在lambda表达式中将oaid写入 sharedPreferences
    @JvmStatic
    fun getOaid(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("oaid", Context.MODE_PRIVATE)
        return sharedPreferences.getString("OAID", "")
    }

    fun getDeviceType(context: Context?): String {
        var type = "Phone"
        try {
            if (context == null) {
                return type
            }
            val b = (context.resources.configuration.screenLayout and 15) >= 3
            type = if (b) {
                "Tablet"
            } else {
                "Phone"
            }
        } catch (throwable: Throwable) {
            Log.e(TAG, throwable.message!!)
        }
        return type
    }

    @JvmStatic
    fun getVersionCode(context: Context): Int {
        try {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.versionCode
        } catch (e: Exception) {
            e.printStackTrace()
            return 1
        }
    }

    @JvmStatic
    fun getVersionName(context: Context): String {
        try {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.versionName
        } catch (e: Exception) {
            e.printStackTrace()
            return "1.0.0"
        }
    }

    @JvmStatic
    fun getAppName(context: Context): String {
        try {
            val packageManager = context.packageManager
            val applicationInfo = packageManager.getApplicationInfo(context.packageName, 0)
            return packageManager.getApplicationLabel(applicationInfo) as String
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return ""
    }

    fun getAssetsData(context: Context): Array<String?>? {
        try {
            val fileNames = context.assets.list("")
            if (fileNames != null && fileNames.size > 0) {
                for (name in fileNames) {
                    if (name == "lion-u-config.json") {
                        val jsonObject = JSONObject(getTxtFromAssets(context, name))
                        val ss = arrayOfNulls<String>(3)
                        ss[0] = jsonObject.optString("appid", "")
                        ss[1] = jsonObject.optString("host", "")
                        ss[2] = jsonObject.optString("planid", "")
                        return ss
                    }
                }
            } else {
                return null
            }
        } catch (ignored: IOException) {
        } catch (ignored: JSONException) {
        }
        return null
    }

    private fun getTxtFromAssets(context: Context, fileName: String): String {
        var result = ""
        try {
            val `is` = context.assets.open(fileName)
            val lenght = `is`.available()
            val buffer = ByteArray(lenght)
            `is`.read(buffer)
            result = String(buffer, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    /**
     * 获取手机的IMEI私有方法
     * @param context
     * @return
     */
    private fun getIMEI(context: Context?): String {
        if (!TextUtils.isEmpty(sImei)) {
            return sImei
        } else {
            var imei = ""
            if (context == null) {
                return imei
            }
            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (telephonyManager != null) {
                try {
                    if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                        imei = telephonyManager.deviceId
                    }
                } catch (throwable: Throwable) {
                    Log.e(TAG, throwable.message!!)
                }
            }
            sImei = imei
            return sImei
        }
    }

    /**
     * 向External文件目录下写入文件
     * TODO should del something when the same name of file or dirs exist
     *
     * @param fileName     要
     * @param content      要写入的内容
     * @param context      Activity Context
     * @throws IOException
     */
    @Throws(IOException::class)
    fun writeExternal(fileName: String?, content: String?, context: Context?): Boolean {
        var result = false
        // 获取External的可用状态
        val storageState = Environment.getExternalStorageState()
        if (storageState == Environment.MEDIA_MOUNTED) {
            // 当External的可用状态为可用时
            // when get the permission
            if (verifyStoragePermissions(context as Activity?) == PackageManager.PERMISSION_GRANTED) {
                try {
                    val sdFile = Environment.getExternalStorageDirectory()
                    val packageSDDir = File(sdFile, "/com.digitalevers.lionsu.sdk/")
                    if (!packageSDDir.exists() || !packageSDDir.isDirectory) {
                        result = packageSDDir.mkdirs() //
                        if (result == true) {
                            //create dir success
                            val filePath = File(packageSDDir, fileName)
                            if (!filePath.exists() || !filePath.isFile) {
                                result = filePath.createNewFile()
                                if (result == true) {
                                    //create file success
                                    val fileOutputStream = FileOutputStream(filePath)
                                    fileOutputStream.write(content!!.toByteArray())
                                    fileOutputStream.close()
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    result = false
                }
            }
        }
        return result
    }

    /**
     * 从External文件目录下读取文件
     *
     * @param fileName 要读取的文件的文件名
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    fun readExternal(fileName: String, context: Context?): String {
        /*File sdFile = Environment.getExternalStorageDirectory();
        File packageSDPath = new File(sdFile,"/com.example.test_library/a.txt");
        FileInputStream fis = new FileInputStream(packageSDPath);

        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        StringBuilder sb = new StringBuilder();
        String line = "";

        while((line = br.readLine())!=null)
        {
            sb.append(line);
        }
        deviceId = sb.toString();
        br.close();*/

        var result = ""
        val stringBuffer = StringBuffer()
        // 获取External的可用状态
        val storageState = Environment.getExternalStorageState()
        if (storageState == Environment.MEDIA_MOUNTED) {
            // 当External的可用状态为可用时
            // 打开文件输入流
            // when get the permission
            if (verifyStoragePermissions(context as Activity?) == PackageManager.PERMISSION_GRANTED) {
                var fileInputStream: FileInputStream? = null
                try {
                    val sdFile = Environment.getExternalStorageDirectory()
                    val packageSDPath = File(sdFile, "/com.digitalevers.lionsu.sdk/$fileName")
                    fileInputStream = FileInputStream(packageSDPath)
                    val buffer = ByteArray(1024)
                    var len = fileInputStream.read(buffer)
                    // 读取文件内容
                    while (len > 0) {
                        stringBuffer.append(String(buffer, 0, len))
                        // 继续把数据存放在buffer中
                        len = fileInputStream.read(buffer)
                    }
                    result = stringBuffer.toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                    result = ""
                }
                // 关闭输入流 oteice : this place maybe create a new exception
                fileInputStream?.close()
            }
        }

        return result
    }
}
