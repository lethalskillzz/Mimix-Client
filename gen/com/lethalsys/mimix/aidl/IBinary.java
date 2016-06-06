/*___Generated_by_IDEA___*/

/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\Ibrahim\\mimix_workspace\\Mimix\\src\\com\\lethalsys\\mimix\\aidl\\IBinary.aidl
 */
package com.lethalsys.mimix.aidl;
public interface IBinary extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.lethalsys.mimix.aidl.IBinary
{
private static final java.lang.String DESCRIPTOR = "com.lethalsys.mimix.aidl.IBinary";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.lethalsys.mimix.aidl.IBinary interface,
 * generating a proxy if needed.
 */
public static com.lethalsys.mimix.aidl.IBinary asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.lethalsys.mimix.aidl.IBinary))) {
return ((com.lethalsys.mimix.aidl.IBinary)iin);
}
return new com.lethalsys.mimix.aidl.IBinary.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_data:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
this.data(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.lethalsys.mimix.aidl.IBinary
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void data(java.lang.String server, java.lang.String uid, java.lang.String user) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(server);
_data.writeString(uid);
_data.writeString(user);
mRemote.transact(Stub.TRANSACTION_data, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_data = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void data(java.lang.String server, java.lang.String uid, java.lang.String user) throws android.os.RemoteException;
}
