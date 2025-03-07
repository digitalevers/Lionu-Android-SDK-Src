package com.digitalevers.lionukt.interfaces

import android.os.Binder
import android.os.IBinder
import android.os.IInterface
import android.os.Parcel

/****************************
 * on 2024/10/10
 */
interface OnePlusIDInterface : IInterface {

    abstract class up : Binder(), OppoIDInterface {

        //companion object{
            class down(var iBinder: IBinder) : OppoIDInterface {
                fun getSerID(str1: String?, str2: String?, str3: String?): String? {
                    var res: String? = null
                    val obtain = Parcel.obtain()
                    val obtain2 = Parcel.obtain()
                    try {
                        obtain.writeInterfaceToken("com.heytap.openid.IOpenID")
                        obtain.writeString(str1)
                        obtain.writeString(str2)
                        obtain.writeString(str3)
                        iBinder.transact(1, obtain, obtain2, 0)
                        obtain2.readException()
                        res = obtain2.readString()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        obtain.recycle()
                        obtain2.recycle()
                    }
                    return res
                }

                override fun asBinder(): IBinder {
                    return iBinder
                }
            }
        companion object{
            fun genInterface(iBinder: IBinder?): OnePlusIDInterface? {
                if (iBinder == null) {
                    return null
                }
                val iInterface = iBinder.queryLocalInterface("com.heytap.openid.IOpenID")
                return if (iInterface == null || iInterface !is OnePlusIDInterface) {
                    OppoIDInterface.up.down(iBinder)
                } else {
                    iInterface
                }
            }
        }

    }
}
