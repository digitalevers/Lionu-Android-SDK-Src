package com.digitalevers.lionukt.util

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

object CheckPermission {
    private const val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.WRITE_EXTERNAL_STORAGE"
    )
    private val TAG: String = CheckPermission::class.java.name

    @JvmStatic
    fun verifyStoragePermissions(activity: Activity?): Int {
        try {
            //检测是否有写的权限
            val permission = ActivityCompat.checkSelfPermission(activity!!, PERMISSIONS_STORAGE[1])
            if (permission != PackageManager.PERMISSION_GRANTED) {
                //Log.e(TAG, String.valueOf(permission));//询问or 拒绝 -1     使用中允许 or  始终允许  0
                // 没有写的权限，去申请写的权限，会弹出对话框
                //ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
            return permission
        } catch (e: Exception) {
            return 1
        }
    }
}