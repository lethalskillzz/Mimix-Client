package com.lethalsys.mimix;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


class Util_Database extends SQLiteOpenHelper {
private static final String DATABASE_NAME="mimix_utildb.db";
private static final int SCHEMA_VERSION=1;
public Util_Database(Context context) {
super(context, DATABASE_NAME, null, SCHEMA_VERSION);
}
@Override
public void onCreate(SQLiteDatabase db) {
	db.execSQL("CREATE TABLE user_table (_id INTEGER PRIMARY KEY AUTOINCREMENT, user_id TEXT, gcm_id TEXT, user_name TEXT, pass_word TEXT );");
	db.execSQL("CREATE TABLE faces (_id INTEGER PRIMARY KEY AUTOINCREMENT, userid TEXT, username TEXT, gender TEXT, location TEXT, phone TEXT, email TEXT, workplace TEXT, occupation TEXT, bio TEXT, reputation TEXT, isVerified TEXT, award TEXT, numfollowers TEXT, numfollowing TEXT  );");
	db.execSQL("CREATE TABLE msglistpostloc (_id INTEGER PRIMARY KEY AUTOINCREMENT, msglistpostloc INTEGER);");
	

}



@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	db.execSQL("DROP TABLE IF EXISTS user_table");
	db.execSQL("DROP TABLE IF EXISTS faces");
	db.execSQL("DROP TABLE IF EXISTS msglistpostloc");
	onCreate(db);
}



public void ClearUSER() {
	getWritableDatabase().execSQL("DELETE FROM "+"user_table"+";");
}


public void insertUSER(String user_id, String gcm_id, String user_name, String pass_word) {
ContentValues cv=new ContentValues();
cv.put("user_id", user_id);
cv.put("gcm_id", gcm_id);
cv.put("user_name", user_name);
cv.put("pass_word", pass_word);
getWritableDatabase().insert("user_table", "user_id", cv);
}


public String getUSER_ID() {
Cursor c = getReadableDatabase().rawQuery("SELECT _id, user_id FROM user_table ORDER BY _id",null);
String rturn=null;
if (c != null)
{
if(c.moveToFirst())
{
	rturn=(c.getString(1));
}
}


return rturn;

}




public String getUSER_GCM_ID() {
Cursor c = getReadableDatabase().rawQuery("SELECT _id, gcm_id FROM user_table ORDER BY _id",null);
String rturn=null;
if (c != null)
{
if(c.moveToFirst())
{
	rturn=(c.getString(1));
}
}


return rturn;

}





public String getUSER() {
Cursor c = getReadableDatabase().rawQuery("SELECT _id, user_name FROM user_table ORDER BY _id",null);
String rturn=null;
if (c != null)
{
if(c.moveToFirst())
{
	rturn=(c.getString(1));
}
}


return rturn;

}



public String getUSER_PASS() {
Cursor c = getReadableDatabase().rawQuery("SELECT _id, pass_word FROM user_table ORDER BY _id",null);
String rturn=null;
if (c != null)
{
if(c.moveToFirst())
{
	rturn=(c.getString(1));
}
}

return rturn;

}






public void ClearMsgListPostLoc() {
	getWritableDatabase().execSQL("DELETE FROM "+"msglistpostloc"+";");
}

public void insertMsgListPostLoc(int postloc) {
ContentValues cv=new ContentValues();
cv.put("msglistpostloc", postloc);
getWritableDatabase().insert("msglistpostloc", "msglistpostloc", cv);
}

public int getMsgListPostLoc() {
	
Cursor c = getReadableDatabase().rawQuery("SELECT _id, msglistpostloc FROM msglistpostloc ORDER BY _id",null);
int rturn=0;
if (c != null)
{
if(c.moveToFirst())
{
	rturn=(c.getInt(1));
}
}

return rturn;

}





public void Clear() {
	getWritableDatabase().execSQL("DELETE FROM "+"faces"+";");
}




public void insertFACE(String userid, String username, String gender, 
		               String phn, String email, String location, String workplace, 
		               String occupation, String bio, String reputation, String isVerified, String award,  String numfollowers, String numfollowing) {
ContentValues cv=new ContentValues();
cv.put("userid", userid);
cv.put("username", username);
cv.put("gender", gender);
cv.put("phone", phn);
cv.put("email", email);
cv.put("location", location);
cv.put("workplace", workplace);
cv.put("occupation", occupation);
cv.put("bio", bio);
cv.put("reputation", reputation);
cv.put("isVerified", isVerified);
cv.put("award", award);
cv.put("numfollowers", numfollowers);
cv.put("numfollowing", numfollowing);
getWritableDatabase().insert("faces", "userid", cv);
}


public void updateFACE(String username, String gender, 
        String phn, String email, String location, String workplace, 
        String occupation, String bio, String reputation, String isVerified, String award,  String numfollowers, String numfollowing) {
ContentValues cv=new ContentValues();
cv.put("gender", gender);
cv.put("phone", phn);
cv.put("email", email);
cv.put("location", location);
cv.put("workplace", workplace);
cv.put("occupation", occupation);
cv.put("bio", bio);
cv.put("reputation", reputation);
cv.put("isVerified", isVerified);
cv.put("award", award);
cv.put("numfollowers", numfollowers);
cv.put("numfollowing", numfollowing);
getWritableDatabase().update("faces", cv, "username="+"'"+username+"'", null);
}



public String getFACEUID(String uname) {
	
Cursor c = getReadableDatabase().rawQuery("SELECT _id, userid FROM faces WHERE username =?",new String[]{uname});
String rturn=null;
if (c != null)
{
if(c.moveToFirst())
{
	rturn=(c.getString(1));
}
}


return rturn;

}



public String getFACENAME(String uid) {
	
Cursor c = getReadableDatabase().rawQuery("SELECT _id, username FROM faces WHERE userid =?",new String[]{uid});
String rturn=null;
if (c != null)
{
if(c.moveToFirst())
{
	rturn=(c.getString(1));
}
}


return rturn;

}






public String getFACEGENDER(String uid) {
	
Cursor c = getReadableDatabase().rawQuery("SELECT _id, gender FROM faces WHERE userid =?",new String[]{uid});
String rturn=null;
if (c != null)
{
if(c.moveToFirst())
{
	rturn=(c.getString(1));
}
}


return rturn;

}




public String getFACELOCATION(String uid) {
	
Cursor c = getReadableDatabase().rawQuery("SELECT _id, location FROM faces WHERE userid =?",new String[]{uid});
String rturn=null;
if (c != null)
{
if(c.moveToFirst())
{
	rturn=(c.getString(1));
}
}


return rturn;

}


public String getFACEPHONE(String uid) {
	
Cursor c = getReadableDatabase().rawQuery("SELECT _id, phone FROM faces WHERE userid =?",new String[]{uid});
String rturn=null;
if (c != null)
{
if(c.moveToFirst())
{
	rturn=(c.getString(1));
}
}


return rturn;

}



public String getFACEEMAIL(String uid) {
	
Cursor c = getReadableDatabase().rawQuery("SELECT _id, email FROM faces WHERE userid =?",new String[]{uid});
String rturn=null;
if (c != null)
{
if(c.moveToFirst())
{
	rturn=(c.getString(1));
}
}


return rturn;

}






public String getFACEWORKPLACE(String uid) {
	
Cursor c = getReadableDatabase().rawQuery("SELECT _id, workplace FROM faces WHERE userid =?",new String[]{uid});
String rturn=null;
if (c != null)
{
if(c.moveToFirst())
{
	rturn=(c.getString(1));
}
}


return rturn;

}


public String getFACEOCCUPATION(String uid) {
	
Cursor c = getReadableDatabase().rawQuery("SELECT _id, occupation FROM faces WHERE userid =?",new String[]{uid});
String rturn=null;
if (c != null)
{
if(c.moveToFirst())
{
	rturn=(c.getString(1));
}
}


return rturn;

}




public String getFACEBIO(String uid) {
	
Cursor c = getReadableDatabase().rawQuery("SELECT _id, bio FROM faces WHERE userid =?",new String[]{uid});
String rturn=null;
if (c != null)
{
if(c.moveToFirst())
{
	rturn=(c.getString(1));
}
}


return rturn;

}






public String getFACEREPUTATION(String uid) {
	
Cursor c = getReadableDatabase().rawQuery("SELECT _id, reputation FROM faces WHERE userid =?",new String[]{uid});
String rturn=null;
if (c != null)
{
if(c.moveToFirst())
{
	rturn=(c.getString(1));
}
}


return rturn;

}



public String getFACEisVERIFIED(String uid) {
	
Cursor c = getReadableDatabase().rawQuery("SELECT _id, isVerified FROM faces WHERE userid =?",new String[]{uid});
String rturn=null;
if (c != null)
{
if(c.moveToFirst())
{
	rturn=(c.getString(1));
}
}

return rturn;

}


public String getFACEAWARD(String uid) {
	
Cursor c = getReadableDatabase().rawQuery("SELECT _id, award FROM faces WHERE userid =?",new String[]{uid});
String rturn=null;
if (c != null)
{
if(c.moveToFirst())
{
	rturn=(c.getString(1));
}
}

return rturn;

}




public String getFACENUMFOLLOWER(String uid) {
	
Cursor c = getReadableDatabase().rawQuery("SELECT _id, numfollowers FROM faces WHERE userid =?",new String[]{uid});
String rturn=null;
if (c != null)
{
if(c.moveToFirst())
{
	rturn=(c.getString(1));
}
}


return rturn;

}



public String getFACENUMFOLLOWING(String uid) {
	
Cursor c = getReadableDatabase().rawQuery("SELECT _id, numfollowing FROM faces WHERE userid =?",new String[]{uid});
String rturn=null;
if (c != null)
{
if(c.moveToFirst())
{
	rturn=(c.getString(1));
}
}


return rturn;

}




public void DltFACE(String uid) {
getReadableDatabase()
.delete("faces", "userid="+uid,null);
}










}