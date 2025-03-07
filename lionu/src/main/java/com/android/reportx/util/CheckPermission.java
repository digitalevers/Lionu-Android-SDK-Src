package com.android.reportx.util;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

public class CheckPermission {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE" };
    private static final String TAG = CheckPermission.class.getName();

    public static int verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,PERMISSIONS_STORAGE[1]);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                //Log.e(TAG, String.valueOf(permission));//询问or 拒绝 -1     使用中允许 or  始终允许  0
                // 没有写的权限，去申请写的权限，会弹出对话框
                //ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
            return permission;
        } catch (Exception e) {
            return 1;
        }
    }
}