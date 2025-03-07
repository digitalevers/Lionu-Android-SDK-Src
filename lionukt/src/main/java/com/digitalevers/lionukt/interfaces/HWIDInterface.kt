package com.digitalevers.lionukt.interfaces

import android.content.Context
import android.os.Build
import android.os.IBinder
import android.os.IInterface
import android.os.Parcel
import android.provider.Settings
import android.text.TextUtils

/****************************
 * on 2024/10/10
 *
 */
interface HWIDInterface : IInterface {
    fun getIDs(): String?

    fun getBoos(): Boolean

    class HWID(private val iBinder: IBinder, private val context: Context) : HWIDInterface {

        override fun asBinder(): IBinder {
            return iBinder
        }

        override fun getIDs(): String? {
            var result: String? = null
            val obtain = Parcel.obtain()
            val obtain2 = Parcel.obtain()

            try {
                obtain.writeInterfaceToken("com.uodis.opendevice.aidl.OpenDeviceIdentifierService")
                iBinder.transact(1, obtain, obtain2, 0)
                obtain2.readException()
                result = obtain2.readString()
            } catch (e: Throwable) {
                obtain.recycle()
                obtain2.recycle()
                e.printStackTrace()
            }

            obtain.recycle()
            obtain2.recycle()
            return result
        }

        override fun getBoos(): Boolean {
            var result = true
            val obtain = Parcel.obtain()
            val obtain2 = Parcel.obtain()

            try {
                obtain.writeInterfaceToken("com.uodis.opendevice.aidl.OpenDeviceIdentifierService")
                iBinder.transact(1, obtain, obtain2, 0)
                obtain2.readException()
                val read = obtain2.readInt()
                if (read == 0) {
                    result = false
                }
            } catch (e: Throwable) {
                obtain.recycle()
                obtain2.recycle()
            }
            obtain.recycle()
            obtain2.recycle()
            return result
        }

        val pPS_oaid: String?
            get() {
                var result: String? = null

                if (Build.VERSION.SDK_INT >= 24) {
                    try {
                        result = Settings.Global.getString(context.contentResolver, "pps_oaid")
                        val z =
                            Settings.Global.getString(context.contentResolver, "pps_track_limit")

                        if (!TextUtils.isEmpty(result) && !TextUtils.isEmpty(z)) {
                            result = "get oaid failed"
                        }
                    } catch (th: Throwable) {
                        th.printStackTrace()
                        result = "get oaid failed"
                    }
                }

                return result
            }
    }
}


