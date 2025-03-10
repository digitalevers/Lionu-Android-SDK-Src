package com.android.reportx.util;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


class HttpUtil {

    private static final int FAIL = 0;
    private static final int SUCCESS = 1;
    private static final int ERROR = 2;

    private static Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    //Toast.makeText(ApplicationProvider.context, "上报成功", Toast.LENGTH_SHORT).show();
                    break;
                case FAIL:
                    //Toast.makeText(ApplicationProvider.context, "接口错误", Toast.LENGTH_SHORT).show();
                    break;
                case ERROR:
                    //Toast.makeText(ApplicationProvider.context, "请求失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    public static void postUsage(Context context, String uri, float amount) {
        if (context == null) {
            return;
        }
        try {
            JSONObject params = new JSONObject();
            params.put("appid", ApplicationProvider.sAppId);
            params.put("planid", ApplicationProvider.sPlanId);
            params.put("os", "1");
            if (!TextUtils.isEmpty(ApplicationProvider.sChannel)) {
                params.put("channel", ApplicationProvider.sChannel);
            }
            if (amount > 0) {
                params.put("amount", String.valueOf(amount));
            }
            JSONObject deviceInfo = new JSONObject();

            deviceInfo.put("applicationId", context.getPackageName());
            deviceInfo.put("appName", DeviceUtil.getAppName(context));
            deviceInfo.put("versionCode", String.valueOf(DeviceUtil.getVersionCode(context)));
            deviceInfo.put("versionName", DeviceUtil.getVersionName(context));
            deviceInfo.put("time", yyyyMMddHHmmss());

            deviceInfo.put("imei", DeviceUtil.getDeviceId(context));        //md5 哈希值
            deviceInfo.put("androidid", DeviceUtil.getAndroidId(context));  //md5 哈希值
            deviceInfo.put("oaid", DeviceUtil.getOaid(context));            //md5 哈希值
            deviceInfo.put("mac", DeviceUtil.getMac(context));              //md5 哈希值
            deviceInfo.put("model", Build.MODEL);
            deviceInfo.put("sys", String.valueOf(Build.VERSION.SDK_INT));
            deviceInfo.put("ua", System.getProperty("http.agent"));
            deviceInfo.put("ip", DeviceUtil.getIPAddress(context));         //md5 哈希值

            params.put("deviceInfo", deviceInfo);
            if (RP.debug) {
                Log.i("RP===>", uri);
                Log.i("RP===>", params.toString());
            }

            post(uri, params.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * post请求基础方法
     * @param uri
     * @param body
     */
    private static void post(String uri, final String body) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(uri);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setUseCaches(false);
                    connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                    connection.connect();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8));
                    writer.write(body);
                    writer.close();
                    int responseCode = connection.getResponseCode();
                    if (RP.debug) {
                        Log.i("RP===>", String.valueOf(responseCode));
                    }
                    if(responseCode == 200){
                        /**
                         * 如果获取的code为200，则证明数据获取是正确的。
                         */
                        InputStream is = connection.getInputStream();
                        String result = readMyInputStream(is);
                        /**
                         * 子线程发送消息到主线程，并将获取的结果带到主线程，让主线程来更新UI。
                         */
                        Message msg = new Message();
                        msg.obj = result;
                        msg.what = SUCCESS;
                        handler.sendMessage(msg);
                    } else {
                        Message msg = new Message();
                        msg.what = FAIL;
                        handler.sendMessage(msg);
                    }
                    connection.disconnect();
                } catch (Exception e) {
                    Log.e("post exception", e.getMessage());
                    /**
                     * 如果获取失败，或出现异常，那么子线程发送失败的消息（FAILURE）到主线程，主线程显示Toast，提示发送请求失败
                     */
                    Message msg = new Message();
                    msg.what = ERROR;
                    handler.sendMessage(msg);
                }
            }
        }).start();

    }

    private static String yyyyMMddHHmmss() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return format.format(new Date());
    }

    /**
     *  读取字节流转为字符串
     * @param is
     * @return
     */
    public static String readMyInputStream(InputStream is) {
        byte[] result;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer))!=-1) {
                baos.write(buffer,0,len);
            }
            is.close();
            baos.close();
            result = baos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            String errorStr = "获取数据失败。";
            return errorStr;
        }
        return new String(result);
    }
}
