package com.android.reportx.util;

import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.android.reportx.helpers.DevicesIDsHelper;


@SuppressLint("StaticFieldLeak")
public class ApplicationProvider extends ContentProvider {

    static Context context;

    private String mOAID;

    private boolean isSupported = false;

    public static String sAppId = "";
    public static String sHost = "";
    public static String sChannel = "";
    public static String sPlanId = "";

    public static String launch_url = "";
    public static String reg_url = "";
    public static String pay_url = "";

    public static String HTTP = "https://"; //Android Q 强制https协议

    @Override
    public boolean onCreate() {
        //Log.i("digitalevers", "contentprovider create");
        context = getContext();
        String[] assetsData = DeviceUtil.getAssetsData(getContext());
        if (assetsData != null && assetsData.length >= 2) {
            sAppId = assetsData[0];
            sHost = assetsData[1];
            sPlanId = assetsData[2];
            //判断shost中是否有协议字符
            if(sHost.contains("http://")){
                sHost = sHost.replace("http://","");
            } else if(sHost.contains("https://")){
                sHost = sHost.replace("https://","");
            }

            launch_url = HTTP + sHost + "/receive/launch";
            reg_url = HTTP + sHost + "/receive/reg";
            pay_url = HTTP + sHost + "/receive/pay";
        }
        try {
            ApplicationInfo info = getContext().getPackageManager().getApplicationInfo(getContext().getPackageName(), PackageManager.GET_META_DATA);
            if (info.metaData != null && info.metaData.containsKey("CHANNEL_KEY")) {
                sChannel = info.metaData.getString("CHANNEL_KEY", "");
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("TAG", "NameNotFoundException==> " + e.getMessage());
        }

        DevicesIDsHelper mDevicesIDsHelper = new DevicesIDsHelper((ids, support) -> {
            if (ids == null || !support) {
                mOAID = "";
                isSupported = support;
            } else {
                isSupported = support;
                mOAID = ids;
            }
            SharedPreferences preferences = getContext().getSharedPreferences("oaid", Context.MODE_PRIVATE);
            if (preferences != null) {
                preferences.edit().putString("OAID", mOAID).apply();
            }
        });

        mDevicesIDsHelper.getOAID(context);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[]
            selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }


}