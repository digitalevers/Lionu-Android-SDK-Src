package com.digitalevers.lionukt.interfaces

import android.os.IBinder
import android.os.IInterface
import android.os.Parcel

/****************************
 * on 2024/10/10
 * 华硕手机 OAID
 *
 */
interface ASUSIDInterface : IInterface {

    class ASUSID(private val iBinder: IBinder) : ASUSIDInterface {

        override fun asBinder(): IBinder {
            return iBinder
        }

        fun getID(): String? {
            var result: String? = null
            val v1 = Parcel.obtain()
            val v2 = Parcel.obtain()

            try {
                v1.writeInterfaceToken("com.asus.msa.SupplementaryDID.IDidAidlInterface")
                iBinder.transact(3, v1, v2, 0)
                v2.readException()
                result = v2.readString()
            } catch (e: Throwable) {
                v1.recycle()
                v2.recycle()
                result = "get oaid failed"
                e.printStackTrace()
            }

            v1.recycle()
            v2.recycle()
            return result
        }

        fun isSupport(): Boolean{
            var support = false
            val obtain = Parcel.obtain()
            val obtain2 = Parcel.obtain()
            try {
                obtain.writeInterfaceToken("com.asus.msa.SupplementaryDID.IDidAidlInterface")

                iBinder.transact(1, obtain, obtain2, 0)
                obtain2.readException()
                if (obtain2.readInt() != 0) {
                    support = true
                }
            } catch (e: Exception) {
                obtain2.recycle()
                obtain.recycle()
            }

            return support
        }
    }
}
