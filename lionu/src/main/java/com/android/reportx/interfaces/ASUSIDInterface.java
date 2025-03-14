package com.android.reportx.interfaces;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;


/****************************
 * on 2019/10/29
 * 华硕手机 OAID
 ****************************
 */
public interface ASUSIDInterface extends IInterface {

    String getID();

    public final class ASUSID implements ASUSIDInterface {
        private IBinder iBinder;

        public ASUSID(IBinder ib) {
            iBinder = ib;
        }

        @Override
        public IBinder asBinder() {
            return iBinder;
        }

        @Override
        public String getID() {
            String result = null;
            Parcel v1 = Parcel.obtain();
            Parcel v2 = Parcel.obtain();

            try {
                v1.writeInterfaceToken("com.asus.msa.SupplementaryDID.IDidAidlInterface");
                iBinder.transact(3, v1, v2, 0);
                v2.readException();
                result = v2.readString();
            } catch (Throwable e) {
                v1.recycle();
                v2.recycle();
                result = "get oaid failed";
                e.printStackTrace();
            }

            v1.recycle();
            v2.recycle();
            return result;
        }

        public boolean isSupport() {
            boolean support = false;
            Parcel obtain = Parcel.obtain();
            Parcel obtain2 = Parcel.obtain();
            try {
                obtain.writeInterfaceToken("com.asus.msa.SupplementaryDID.IDidAidlInterface");

                iBinder.transact(1, obtain, obtain2, 0);
                obtain2.readException();
                if (obtain2.readInt() != 0) {
                    support = true;
                }

            } catch (Exception e){
                obtain2.recycle();
                obtain.recycle();
            }

            return support;
        }
    }
}
