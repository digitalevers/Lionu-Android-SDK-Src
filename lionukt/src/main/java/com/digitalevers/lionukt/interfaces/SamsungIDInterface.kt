package com.digitalevers.lionukt.interfaces

import android.os.Binder
import android.os.IBinder
import android.os.IInterface
import android.os.Parcel

interface SamsungIDInterface : IInterface {
    fun getID(): String?

    class Proxy(private val mIBinder: IBinder) : SamsungIDInterface {
        override fun asBinder(): IBinder {
            return mIBinder
        }

        override fun getID(): String? {
            var result: String? = null
            val obtain = Parcel.obtain()
            val obtain2 = Parcel.obtain()
            try {
                obtain.writeInterfaceToken("com.samsung.android.deviceidservice.IDeviceIdService")
                mIBinder.transact(1, obtain, obtain2, 0)
                obtain2.readException()
                result = obtain2.readString()
            } catch (t: Throwable) {
                obtain2.recycle()
                obtain.recycle()
                t.printStackTrace()
            }
            obtain2.recycle()
            obtain.recycle()

            return result
        }
    }

    abstract class Stub : Binder(), SamsungIDInterface {
        init {
            this.attachInterface(
                (this as IInterface),
                "com.samsung.android.deviceidservice.IDeviceIdService"
            )
        }

        fun a(iBinder: IBinder?): SamsungIDInterface? {
            if (iBinder == null) {
                return null
            }
            val iInterface =
                iBinder.queryLocalInterface("com.samsung.android.deviceidservice.IDeviceIdService")
                    ?: return null
            val proxy = Proxy(iBinder)
            return proxy
        }
    }
}
