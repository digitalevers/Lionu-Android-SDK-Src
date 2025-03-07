
package com.android.reportx.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaDrm;
import android.media.UnsupportedSchemeException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;

import static android.content.Context.TELEPHONY_SERVICE;
import static android.content.Context.WIFI_SERVICE;


public class DeviceUtil {

    private static final String TAG = DeviceUtil.class.getName();

    private static String sWifiMac;
    private static String sImei;
    private static String sMeid;

    static {
        sWifiMac = "";
        sImei = "";
        sMeid = "";
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




    public static String getAndroidId(Context context) {
        String androidId = null;
        if (context != null) {
            try {
                androidId = Secure.getString(context.getContentResolver(), "android_id");
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return androidId;
    }

    /**
     * 获取 Android 设备序列号
     * @return
     */
    public static String getSerial(Context context) {
        if (VERSION.SDK_INT >= 9) {
            String serial = "";
            int sdkInt = VERSION.SDK_INT;
            if (sdkInt >= 26) {
                try {
                    Class clazz = Class.forName("android.os.Build");
                    Method method = clazz.getMethod("getSerial");
                    serial = (String) method.invoke(clazz);
                } catch (Throwable throwable) {
                    /*if(throwable.getMessage() != null) {
                        Log.e(TAG, throwable.getMessage());
                    }*/
                    System.out.println(throwable.getMessage());
                }
            } else {
                serial = Build.SERIAL;
            }
            return serial;
        } else {
            return "";
        }
    }

    private static String getSerialNo(Context context) {
        String serialNo = "";
        if (VERSION.SDK_INT >= 26) {
            try {
                Class clazz = Class.forName("android.os.Build");
                Method method = clazz.getMethod("getSerial");
                serialNo = (String) method.invoke(clazz);
            } catch (Throwable throwable) {
                Log.e(TAG, throwable.getMessage());
            }
        } else {
            serialNo = Build.SERIAL;
        }
        return serialNo;
    }


    private static boolean checkPermission(Context context, String permissions) {
        boolean isGranted = false;
        if (context == null) {
            return isGranted;
        } else {
            if (VERSION.SDK_INT >= 23) {
                try {
                    Class clazz = Class.forName("android.content.Context");
                    Method method = clazz.getMethod("checkSelfPermission", String.class);
                    int result = (Integer) method.invoke(context, permissions);
                    if (result == 0) {
                        isGranted = true;
                    } else {
                        isGranted = false;
                    }
                } catch (Throwable throwable) {
                    isGranted = false;
                    Log.e(TAG, throwable.getMessage());
                }
            } else {
                PackageManager packageManager = context.getPackageManager();
                if (packageManager.checkPermission(permissions, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                    isGranted = true;
                }
            }
            return isGranted;
        }
    }


    private static String getMacByJavaAPI() {
        try {
            Enumeration enumeration = NetworkInterface.getNetworkInterfaces();
            NetworkInterface networkInterface;
            do {
                if (!enumeration.hasMoreElements()) {
                    return null;
                }
                networkInterface = (NetworkInterface) enumeration.nextElement();
            } while (!"wlan0".equals(networkInterface.getName()) && !"eth0".equals(networkInterface.getName()));

            byte[] hardwareAddress = networkInterface.getHardwareAddress();
            if (hardwareAddress != null && hardwareAddress.length != 0) {
                StringBuilder builder = new StringBuilder();
                byte[] address = hardwareAddress;
                int length = hardwareAddress.length;

                for (int i = 0; i < length; ++i) {
                    byte b = address[i];
                    builder.append(String.format("%02X:", b));
                }
                if (builder.length() > 0) {
                    builder.deleteCharAt(builder.length() - 1);
                }
                return builder.toString().toLowerCase(Locale.getDefault());
            }
            return null;

        } catch (Throwable throwable) {
            Log.e(TAG, throwable.getMessage());
        }
        return null;
    }

    private static String getMacShell() {
        String[] addressStr = new String[]{"/sys/class/net/wlan0/address", "/sys/class/net/eth0/address", "/sys/devices/virtual/net/wlan0/address"};
        try {
            for (int i = 0; i < addressStr.length; ++i) {
                try {
                    String reaAddress = reaMac(addressStr[i]);
                    if (reaAddress != null) {
                        return reaAddress;
                    }
                } catch (Throwable throwable) {
                    Log.e(TAG, throwable.getMessage());
                }
            }
        } catch (Throwable throwable) {
            Log.e(TAG, throwable.getMessage());
        }
        return null;
    }

    private static String reaMac(String s) {
        String mac = null;
        try {
            FileReader fileReader = new FileReader(s);
            BufferedReader bufferedReader = null;
            if (fileReader != null) {
                try {
                    bufferedReader = new BufferedReader(fileReader, 1024);
                    mac = bufferedReader.readLine();
                } finally {
                    if (fileReader != null) {
                        try {
                            fileReader.close();
                        } catch (Throwable throwable) {
                            Log.e(TAG, throwable.getMessage());
                        }
                    }
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (Throwable throwable) {
                            Log.e(TAG, throwable.getMessage());
                        }
                    }
                }
            }
        } catch (Throwable throwable) {
            Log.e(TAG, throwable.getMessage());
        }
        return mac;
    }


    public static String getMeid(Context context) {
        String meid = null;
        if (context == null) {
            return null;
        } else {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            if (telephonyManager == null) {
                return null;
            } else {
                try {
                    if (checkPermission(context, android.Manifest.permission.READ_PHONE_STATE) && telephonyManager != null) {
                        if (VERSION.SDK_INT < 26) {
                            meid = getIMEI(context);
                        } else {
                            try {
                                meid = meid(context);
                                if (TextUtils.isEmpty(meid)) {
                                    meid = getIMEI(context);
                                }
                            } catch (Throwable throwable) {
                                Log.e(TAG, throwable.getMessage());
                            }
                        }
                    }
                } catch (Throwable throwable) {
                    Log.e(TAG, throwable.getMessage());
                }
            }
            return meid;
        }
    }

    private static String meid(Context context) {
        if (TextUtils.isEmpty(sMeid)) {
            return sMeid;
        } else {
            String meid = null;
            if (context == null) {
                return null;
            } else {
                try {
                    Class clazz = Class.forName("android.telephony.TelephonyManager");
                    Method method = clazz.getMethod("getMeid");
                    Object obj = method.invoke((Object) null);
                    if (null != obj && obj instanceof String) {
                        meid = (String) obj;
                    }
                } catch (Throwable throwable) {
                    Log.e(TAG, throwable.getMessage());
                }
                sMeid = meid;
                return sMeid;
            }
        }
    }

    /**
     * 获取局域网ip地质
     * @param context
     * @return
     */
    public static String getIPAddress(Context context) {
        String ipv4Host = null;
        String ipv6Host = null;
        try {
            Enumeration enumeration = NetworkInterface.getNetworkInterfaces();
            Iterator iterator = Collections.list(enumeration).iterator();

            while (iterator.hasNext()) {
                NetworkInterface networkInterface = (NetworkInterface) iterator.next();
                Enumeration inetAddresses = networkInterface.getInetAddresses();
                Iterator addressIterator = Collections.list(inetAddresses).iterator();

                while (addressIterator.hasNext()) {
                    InetAddress inetAddress = (InetAddress) addressIterator.next();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address && !networkInterface.getDisplayName().contains("dummy") && ipv4Host == null) {
                        ipv4Host = inetAddress.getHostAddress();
                    }

                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet6Address && !networkInterface.getDisplayName().contains("dummy") && ipv6Host == null) {
                        ipv6Host = inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException socketException) {
            ipv4Host = "SocketException";
            Log.e(TAG, socketException.getMessage());
        }
        if (ipv4Host != null) {
            return ipv4Host;
        } else {
            ipv4Host = ipv6Host;
            if (ipv6Host == null) {
                ipv4Host = "null";
            }
            return ipv4Host;
        }

    }


    /**
     * 获取mac地址
     * @param context
     * @return
     */
    public static String getMac(Context context) {
        if (!TextUtils.isEmpty(sWifiMac)) {
            return sWifiMac;
        } else {
            String mac = "";
            if (context == null) {
                return mac;
            }
            if (VERSION.SDK_INT < 23) {
                mac = getMacBySystemInterface(context);
            } else if (VERSION.SDK_INT == 23) {
                mac = getMacByJavaAPI();
                if (TextUtils.isEmpty(mac)) {
                    mac = getMacShell();
                }
            } else {
                mac = getMacByJavaAPI();
                if (TextUtils.isEmpty(mac)) {
                    mac = getMacBySystemInterface(context);
                }
            }
            sWifiMac = mac;
            return sWifiMac;
        }
    }

    private static String getMacBySystemInterface(Context context) {
        if (context == null) {
            return "";
        } else {
            try {
                WifiManager manager = (WifiManager) context.getSystemService(WIFI_SERVICE);
                if (checkPermission(context, android.Manifest.permission.ACCESS_WIFI_STATE)) {
                    if (manager != null) {
                        WifiInfo wifiInfo = manager.getConnectionInfo();
                        return wifiInfo.getMacAddress();
                    } else {
                        return "";
                    }
                } else {
                    return "";
                }
            } catch (Throwable throwable) {
                Log.e(TAG, throwable.getMessage());
                return "";
            }
        }
    }


    public static String getPackageName(Context context) {
        return context == null ? null : context.getPackageName();
    }

    /**
     * 获取IMEI封装方法
     * API低于29 直接获取
     * API高于29 模拟生成一个IMEI
     * @param context
     * @return md5哈希值
     */
    public static String getDeviceId(Context context) {
        String deviceId = "";
        if (context != null) {
            try {
                SharedPreferences preferences = context.getSharedPreferences("GUID", Context.MODE_PRIVATE);
                deviceId = preferences.getString("imei", "");
                if (TextUtils.isEmpty(deviceId)) {
                    deviceId = readExternal("imei.txt",context);
                    if(TextUtils.isEmpty(deviceId)){
                        if (VERSION.SDK_INT < 29) {
                            deviceId = getIMEI(context);
                        } else {
                            deviceId = randomNum(15)+"m";
                        }
                        //write deviceId to SD Card
                        writeExternal("imei.txt",deviceId, context);
                    }
                    preferences.edit().putString("imei", deviceId).apply();
                }

                /*if (TextUtils.isEmpty(deviceId)) {
                    deviceId = getSerialNo();
                }*/
            } catch (Throwable throwable) {
                Log.e(TAG, throwable.getMessage());
            }
            /*if (TextUtils.isEmpty(deviceId)) {
                deviceId = getGUID(context);
            }*/
        }
        return deviceId;
    }


    /**
     * 获取数字版权管理设备ID
     *
     * @return WidevineID，可能为空
     */
    public static String getWidevineID() {
        try {
            //See https://stackoverflow.com/questions/16369818/how-to-get-crypto-scheme-uuid
            //You can find some UUIDs in the https://github.com/google/ExoPlayer source code
            final UUID WIDEVINE_UUID = new UUID(0xEDEF8BA979D64ACEL, 0xA3C827DCD51D21EDL);
            MediaDrm mediaDrm = new MediaDrm(WIDEVINE_UUID);
            byte[] widevineId = mediaDrm.getPropertyByteArray(MediaDrm.PROPERTY_DEVICE_UNIQUE_ID);
            if (widevineId == null) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            for (byte aByte : widevineId) {
                sb.append(String.format("%02x", aByte));
            }
            return sb.toString();
        } catch (UnsupportedSchemeException e) {
            Log.e(TAG,e.toString());
        }
        return "";
    }

    /**
     * 模拟 factor 位数的随即数字串
     * @param factor
     * @return
     */
    private static Long randomNum(int factor){
        return new Double((Math.random() + 1) * Math.pow(10, factor - 1)).longValue();
    }

    /**
     * 获取 Android 设备唯一码 uuid
     * @param context
     * @return
     */
    public static String getGUID(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("GUID", Context.MODE_PRIVATE);
        String uuid = preferences.getString("uuid", "");
        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
            preferences.edit().putString("uuid", uuid).apply();
        }
        return uuid;
    }

    public static String getOaid(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("oaid", Context.MODE_PRIVATE);
        return sharedPreferences.getString("OAID", "");
    }



    public static String getDeviceType(Context context) {
        String type = "Phone";
        try {
            if (context == null) {
                return type;
            }
            boolean b = (context.getResources().getConfiguration().screenLayout & 15) >= 3;
            if (b) {
                type = "Tablet";
            } else {
                type = "Phone";
            }
        } catch (Throwable throwable) {
            Log.e(TAG, throwable.getMessage());
        }
        return type;
    }

    public static int getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "1.0.0";
        }
    }

    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            return (String) packageManager.getApplicationLabel(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String[] getAssetsData(Context context) {
        try {
            String[] fileNames = context.getAssets().list("");
            if (fileNames != null && fileNames.length > 0) {
                for (String name : fileNames) {
                    if (name.equals("lion-u-config.json")) {
                        JSONObject jsonObject = new JSONObject(getTxtFromAssets(context, name));
                        String[] ss = new String[3];
                        ss[0] = jsonObject.optString("appid", "");
                        ss[1] = jsonObject.optString("host", "");
                        ss[2] = jsonObject.optString("planid", "");
                        return ss;
                    }
                }
            } else {
                return null;
            }

        } catch (IOException | JSONException ignored) {

        }
        return null;
    }

    private static String getTxtFromAssets(Context context, String fileName) {
        String result = "";
        try {
            InputStream is = context.getAssets().open(fileName);
            int lenght = is.available();
            byte[] buffer = new byte[lenght];
            is.read(buffer);
            result = new String(buffer, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取手机的IMEI私有方法
     * @param context
     * @return
     */
    private static String getIMEI(Context context) {
        if (!TextUtils.isEmpty(sImei)) {
            return sImei;
        } else {
            String imei = "";
            if (context == null) {
                return imei;
            }
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                try {
                    if (checkPermission(context, android.Manifest.permission.READ_PHONE_STATE)) {
                        imei = telephonyManager.getDeviceId();
                    }
                } catch (Throwable throwable) {
                    Log.e(TAG, throwable.getMessage());
                }
            }
            sImei = imei;
            return sImei;
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
    public static boolean writeExternal(String fileName,String content,Context context) throws IOException {
        boolean result = false;
        // 获取External的可用状态
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            // 当External的可用状态为可用时
            // when get the permission
            if(CheckPermission.verifyStoragePermissions((Activity) context) == PackageManager.PERMISSION_GRANTED){
                try{
                    File sdFile = Environment.getExternalStorageDirectory();
                    File packageSDDir = new File(sdFile,"/com.digitalevers.lionsu.sdk/");
                    if(!packageSDDir.exists() || !packageSDDir.isDirectory()){
                        result = packageSDDir.mkdirs();//
                        if(result == true){
                            //create dir success
                            File filePath = new File(packageSDDir,fileName);
                            if(!filePath.exists() || !filePath.isFile()){
                                result = filePath.createNewFile();
                                if(result == true){
                                    //create file success
                                    FileOutputStream fileOutputStream = new FileOutputStream(filePath);
                                    fileOutputStream.write(content.getBytes());
                                    fileOutputStream.close();
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    result = false;
                }
            }

        }
        return result;
    }

    /**
     * 从External文件目录下读取文件
     *
     * @param fileName 要读取的文件的文件名
     * @return
     * @throws IOException
     */
    public static String readExternal(String fileName,Context context) throws IOException {
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

        String result = "";
        StringBuffer stringBuffer = new StringBuffer();
        // 获取External的可用状态
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            // 当External的可用状态为可用时
            // 打开文件输入流
            // when get the permission
            if(CheckPermission.verifyStoragePermissions((Activity) context) == PackageManager.PERMISSION_GRANTED){
                FileInputStream fileInputStream = null;
                try {
                    File sdFile = Environment.getExternalStorageDirectory();
                    File packageSDPath = new File(sdFile,"/com.digitalevers.lionsu.sdk/"+fileName);
                    fileInputStream = new FileInputStream(packageSDPath);
                    byte[] buffer = new byte[1024];
                    int len = fileInputStream.read(buffer);
                    // 读取文件内容
                    while (len > 0) {
                        stringBuffer.append(new String(buffer, 0, len));
                        // 继续把数据存放在buffer中
                        len = fileInputStream.read(buffer);
                    }
                    result = stringBuffer.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    result = "";
                }
                // 关闭输入流 oteice : this place maybe create a new exception
                if(fileInputStream != null) {
                    fileInputStream.close();
                }
            }

        }

        return result;
    }
}
