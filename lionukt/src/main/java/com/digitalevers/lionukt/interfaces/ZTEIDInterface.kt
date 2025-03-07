package com.digitalevers.lionukt.interfaces

import android.os.Binder
import android.os.IBinder
import android.os.IInterface
import android.os.Parcel

interface ZTEIDInterface : IInterface {
    fun c(): Boolean
    fun getOAID(): String?
    fun isSupported(): Boolean
    fun shutDown()

    abstract class up : Binder(), ZTEIDInterface {
        class down(private val binder: IBinder) : ZTEIDInterface {
            override fun asBinder(): IBinder {
                return binder
            }

            override fun c(): Boolean {
                var v0 = false
                val obtain = Parcel.obtain()
                val obtain2 = Parcel.obtain()
                try {
                    obtain.writeInterfaceToken("com.bun.lib.MsaIdInterface")
                    binder.transact(2, obtain, obtain2, 0)
                    obtain2.readException()
                    if (obtain2.readInt() == 0) {
                        obtain2.recycle()
                        obtain.recycle()
                        v0 = true
                    }
                } catch (v0_1: Throwable) {
                    obtain2.recycle()
                    obtain.recycle()
                    v0_1.printStackTrace()
                }
                return v0
            }

            override fun getOAID(): String? {
                var result: String? = null
                val obtain = Parcel.obtain()
                val obtain2 = Parcel.obtain()
                try {
                    obtain.writeInterfaceToken("com.bun.lib.MsaIdInterface")
                    binder.transact(3, obtain, obtain2, 0)
                    obtain2.readException()
                    result = obtain2.readString()
                } catch (v0: Throwable) {
                    obtain.recycle()
                    obtain2.recycle()
                }

                obtain.recycle()
                obtain2.recycle()
                return result
            }


            override fun isSupported(): Boolean {
                val obtain = Parcel.obtain()
                val obtain2 = Parcel.obtain()
                var result = false

                try {
                    obtain.writeInterfaceToken("com.bun.lib.MsaIdInterface")
                    binder.transact(1, obtain, obtain2, 0)
                    obtain2.readException()
                    if (obtain2.readInt() != 0) {
                        result = true
                    }
                } catch (e: Exception) {
                    obtain2.recycle()
                    obtain.recycle()
                }
                return result
            }

            override fun shutDown() {
                val obtain = Parcel.obtain()
                val obtain2 = Parcel.obtain()
                try {
                    obtain.writeInterfaceToken("com.bun.lib.MsaIdInterface")
                    binder.transact(6, obtain, obtain2, 0)
                    obtain2.readException()
                } catch (v0: Throwable) {
                    obtain2.recycle()
                    obtain.recycle()
                }
                obtain2.recycle()
                obtain.recycle()
            }
        }
    }
}
