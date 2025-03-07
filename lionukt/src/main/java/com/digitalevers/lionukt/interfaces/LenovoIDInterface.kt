package com.digitalevers.lionukt.interfaces

import android.os.Binder
import android.os.IBinder
import android.os.IInterface
import android.os.Parcel
import android.os.RemoteException

/****************************
 * on 2024/10/10
 * 获取联想手机 OAID
 */
interface LenovoIDInterface : IInterface {
    fun a(): String?

    fun a(arg1: String?): String?

    fun b(): String?

    fun b(arg1: String?): String?

    fun c(): Boolean

    abstract class len_up : Binder(), LenovoIDInterface {
        class len_down(private val iBinder: IBinder) : LenovoIDInterface {
            override fun asBinder(): IBinder? {
                return null
            }

            override fun a(): String? {
                var readString: String? = null
                val obtain = Parcel.obtain()
                val obtain2 = Parcel.obtain()
                try {
                    obtain.writeInterfaceToken("com.zui.deviceidservice.IDeviceidInterface")
                    iBinder.transact(1, obtain, obtain2, 0)
                    obtain2.readException()
                    readString = obtain2.readString()
                    return readString
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    obtain2.recycle()
                    obtain.recycle()
                }
                return readString
            }

            override fun a(arg1: String?): String? {
                var readString: String? = null
                val obtain = Parcel.obtain()
                val obtain2 = Parcel.obtain()
                try {
                    obtain.writeInterfaceToken("com.zui.deviceidservice.IDeviceidInterface")
                    iBinder.transact(4, obtain, obtain2, 0)
                    obtain2.readException()
                    readString = obtain2.readString()
                    return readString
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    obtain2.recycle()
                    obtain.recycle()
                }
                return readString
            }

            override fun b(): String? {
                var readString: String? = null
                val obtain = Parcel.obtain()
                val obtain2 = Parcel.obtain()
                try {
                    obtain.writeInterfaceToken("com.zui.deviceidservice.IDeviceidInterface")
                    iBinder.transact(2, obtain, obtain2, 0)
                    obtain2.readException()
                    readString = obtain2.readString()
                    return readString
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    obtain2.recycle()
                    obtain.recycle()
                }
                return readString
            }

            override fun b(arg1: String?): String? {
                var readString: String? = null
                val obtain = Parcel.obtain()
                val obtain2 = Parcel.obtain()
                try {
                    obtain.writeInterfaceToken("com.zui.deviceidservice.IDeviceidInterface")
                    iBinder.transact(5, obtain, obtain2, 0)
                    obtain2.readException()
                    readString = obtain2.readString()
                    return readString
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    obtain2.recycle()
                    obtain.recycle()
                }
                return readString
            }

            override fun c(): Boolean {
                var support = false
                val obtain = Parcel.obtain()
                val obtain2 = Parcel.obtain()
                try {
                    obtain.writeInterfaceToken("com.zui.deviceidservice.IDeviceidInterface")
                    iBinder.transact(3, obtain, obtain2, 0)
                    obtain2.readException()

                    if (obtain2.readInt() != 0) {
                        support = true
                    }

                    obtain2.recycle()
                    obtain.recycle()
                } catch (th: Throwable) {
                    obtain2.recycle()
                    obtain.recycle()
                }

                return support
            }
        }


        @Throws(RemoteException::class)
        override fun onTransact(
            code: Int, data: Parcel,
            reply: Parcel?, flags: Int
        ): Boolean {
            var str: String? = "com.zui.deviceidservice.IDeviceidInterface"
            when (code) {
                1 -> {
                    data.enforceInterface(str!!)
                    str = a()
                    reply!!.writeNoException()
                    reply.writeString(str)
                    return true
                }

                2 -> {
                    data.enforceInterface(str!!)
                    str = b()
                    reply!!.writeNoException()
                    reply.writeString(str)
                    return true
                }

                3 -> {
                    data.enforceInterface(str!!)
                    val c = c()
                    reply!!.writeNoException()
                    reply.writeInt(if (c) 1 else 0)
                    return true
                }

                4 -> {
                    data.enforceInterface(str!!)
                    str = a(data.readString())
                    reply!!.writeNoException()
                    reply.writeString(str)
                    return true
                }

                5 -> {
                    data.enforceInterface(str!!)
                    str = b(data.readString())
                    reply!!.writeNoException()
                    reply.writeString(str)
                    return true
                }

                1598968902 -> {
                    reply!!.writeString(str)
                    return true
                }

                else -> return super.onTransact(code, data, reply, flags)

            }
        }

        companion object {
            fun getHelper(iBinder: IBinder?): LenovoIDInterface? {
                if (iBinder == null) {
                    return null
                }
                val iInterface =
                    iBinder.queryLocalInterface("com.zui.deviceidservice.IDeviceidInterface")
                return if (iInterface == null || iInterface !is LenovoIDInterface) {
                    len_down(iBinder)
                } else {
                    iInterface
                }
            }
        }
    }
}
