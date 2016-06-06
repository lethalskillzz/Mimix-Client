package com.lethalsys.mimix;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


class Database_Two extends SQLiteOpenHelper {
private static final String DATABASE_NAME="mimix_db2.db";
private static final int SCHEMA_VERSION=1;
public Database_Two(Context context) {
super(context, DATABASE_NAME, null, SCHEMA_VERSION);
}

@Override
public void onCreate(SQLiteDatabase dbase) {
	dbase.execSQL("CREATE TABLE contactsTable (_id INTEGER PRIMARY KEY AUTOINCREMENT, contactname TEXT, detail TEXT, userid TEXT, following TEXT);");
	dbase.execSQL("CREATE TABLE allcontactsTable (_id INTEGER PRIMARY KEY AUTOINCREMENT, contactname TEXT, detail Text, userid TEXT, following TEXT);");
	dbase.execSQL("CREATE TABLE msgListTable (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT, username TEXT, msgbody TEXT, status TEXT, mid TEXT, stamp TEXT);");
	dbase.execSQL("CREATE TABLE notificationsTable (_id INTEGER PRIMARY KEY AUTOINCREMENT, nid TEXT, type TEXT, data TEXT, body TEXT, stamp TEXT, uid TEXT, username TEXT);");
	dbase.execSQL("CREATE TABLE hashTable (_id INTEGER PRIMARY KEY AUTOINCREMENT,postid TEXT, userid TEXT, name TEXT, body TEXT, pimage BLOB, date TEXT, type TEXT, rating TEXT, ratecount TEXT, commentcount TEXT, isExpanded TEXT);");
	dbase.execSQL("CREATE TABLE maincontactsTable (_id INTEGER PRIMARY KEY AUTOINCREMENT, contactname TEXT, detail Text, userid TEXT, following TEXT);");
	dbase.execSQL("CREATE TABLE trendinghashTable (_id INTEGER PRIMARY KEY AUTOINCREMENT, hashtag TEXT, count TEXT);");
	dbase.execSQL("CREATE TABLE searchTable (_id INTEGER PRIMARY KEY AUTOINCREMENT, userid TEXT, resulttitle TEXT, resultdetail TEXT);");
}



@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	
	db.execSQL("DROP TABLE IF EXISTS contactsTable");
	db.execSQL("DROP TABLE IF EXISTS allcontactsTable");
	db.execSQL("DROP TABLE IF EXISTS msgListTable");
	db.execSQL("DROP TABLE IF EXISTS notificationsTable");
	db.execSQL("DROP TABLE IF EXISTS hashTable");
	db.execSQL("DROP TABLE IF EXISTS maincontactsTable");
	db.execSQL("DROP TABLE IF EXISTS trendinghashTable");
	db.execSQL("DROP TABLE IF EXISTS searchTable");

	onCreate(db);
}


public void Clear() {
	getWritableDatabase().execSQL("DELETE FROM "+"contactsTable"+";");
}

public void ClearALL() {
	getWritableDatabase().execSQL("DELETE FROM "+"allcontactsTable"+";");
}

public void ClearMsgList() {
	getWritableDatabase().execSQL("DELETE FROM "+"msgListTable"+";");
}


public void ClearNotifications() {
	getWritableDatabase().execSQL("DELETE FROM "+"notificationsTable"+";");
}


public void ClearHash() {
	getWritableDatabase().execSQL("DELETE FROM "+"HashTable"+";");
}

public void ClearMAIN() {
	getWritableDatabase().execSQL("DELETE FROM "+"maincontactsTable"+";");
}


public void ClearTrendingHash() {
	getWritableDatabase().execSQL("DELETE FROM "+"trendinghashTable"+";");
}


public void ClearSearch() {
	getWritableDatabase().execSQL("DELETE FROM "+"searchTable"+";");
}


public void insert(String contactname, String detail, String userid, String following) {
ContentValues cv=new ContentValues();
cv.put("contactname", contactname);
cv.put("detail", detail);
cv.put("userid", userid);
cv.put("following", following);
getWritableDatabase().insert("contactsTable", "contactname", cv);
}


public void insertALL(String contactname, String detail, String userid, String following) {
ContentValues cv=new ContentValues();
cv.put("contactname", contactname);
cv.put("detail", detail);
cv.put("userid", userid);
cv.put("following", following);
getWritableDatabase().insert("allcontactsTable", "contactname", cv);
}


public void insertMsgList(String userid, String username, String msgbody, String status, String mid, String stamp) {
ContentValues cv=new ContentValues();
cv.put("uid", userid);
cv.put("username", username);
cv.put("msgbody", msgbody);
cv.put("status", status);
cv.put("mid", mid);
cv.put("stamp", stamp);
getWritableDatabase().insert("msgListTable", "username", cv);
}


public void insertHash(String postid, String userid, String name, String body,
byte[] pimage, String date, String type, String rating, String ratecount, String commentcount) {
ContentValues cv=new ContentValues();
cv.put("postid", postid);
cv.put("userid", userid);
cv.put("name", name);
cv.put("body", body);
cv.put("pimage", pimage);
cv.put("date", date);
cv.put("type", type);
cv.put("rating", rating);
cv.put("ratecount", ratecount);
cv.put("commentcount", commentcount);
cv.put("isExpanded", "NO"); 
getWritableDatabase().insert("hashTable", "postid", cv);
}


public void insertMAIN(String contactname, String detail, String userid, String following) {
ContentValues cv=new ContentValues();
cv.put("contactname", contactname);
cv.put("detail", detail);
cv.put("userid", userid);
cv.put("following", following);
getWritableDatabase().insert("maincontactsTable", "contactname", cv);
}



public void insertTrendingHash(String hashtag, String count) {
ContentValues cv=new ContentValues();
cv.put("hashtag", hashtag);
cv.put("count", count);
getWritableDatabase().insert("trendinghashTable", "hashtag", cv);
}



public void insertNotifications(String nid, String type, String data, String body, String stamp, String uid, String username) {
ContentValues cv=new ContentValues();
cv.put("nid", nid);
cv.put("type", type);
cv.put("data", data);
cv.put("body", body);
cv.put("stamp", stamp);
cv.put("uid", uid);
cv.put("username", username);
getWritableDatabase().insert("notificationsTable", "nid", cv);
}


public void insertSearch(String userid, String title, String detail) {
ContentValues cv=new ContentValues();
cv.put("userid", userid);
cv.put("resulttitle", title);
cv.put("resultdetail", detail);
getWritableDatabase().insert("searchTable", "userid", cv);
}


public void updatemsglist(String mid)
{
	ContentValues cv=new ContentValues();
	cv.put("status", "read");
	getWritableDatabase().update("msgListTable", cv, "mid="+mid, null);
	
}




public void update(String contact,String isfollow)
{
	ContentValues cv=new ContentValues();
	cv.put("following", isfollow);
	getWritableDatabase().update("contactsTable", cv, "contactname="+contact, null);
}


public Cursor getAllcontacts() {
return(getReadableDatabase()
.rawQuery("SELECT _id, contactname, detail, userid, following FROM contactsTable ORDER BY contactname",
null));
}

public String getcontactName(Cursor c) {
return(c.getString(1));
}

public String getcontactDetail(Cursor c) {
return(c.getString(2));
}

public String getcontactUID(Cursor c) {
return(c.getString(3));
}

public String getFollowing(Cursor c) {
return(c.getString(4));
}

/*public String getDate(Cursor c) {
return(c.getString(3));
}*/



public void updateALL(String contact,String isfollow)
{
	ContentValues cv=new ContentValues();
	cv.put("following", isfollow);
	getWritableDatabase().update("allcontactsTable", cv, "contactname="+contact, null);
}




public String getLastALLcontacts() {
Cursor c = getReadableDatabase().rawQuery("SELECT * FROM allcontactsTable ORDER BY _id DESC ",null);
String rturn=null;
if (c != null)  
{
if(c.moveToFirst())
{
	rturn = String.valueOf(c.getCount());
}
}
return rturn;
}




public Cursor getAllALLcontacts() {
return(getReadableDatabase()
.rawQuery("SELECT _id, contactname, detail, userid, following FROM allcontactsTable ORDER BY contactname",
null));
}

public String getALLcontactName(Cursor c) {
return(c.getString(1));
}


public String getALLcontactDetail(Cursor c) {
return(c.getString(2));
}

public String getALLcontactUID(Cursor c) {
return(c.getString(3));
}

public String getALLFollowing(Cursor c) {
return(c.getString(4));
}


public Cursor getALLCONTACTUSER() {
return(getReadableDatabase()
.rawQuery("SELECT _id, contactname FROM allcontactsTable ORDER BY _id ASC",
null));
}


/*public String GETALLcontactsName(String uname) {
	
Cursor c = getReadableDatabase().rawQuery("SELECT _id, IMG FROM allcontactsTable WHERE contactname =?",new String[]{uname});
String rturn=null;
if (c != null)
{
if(c.moveToFirst())
{
	rturn=(c.getString(1));
}
}
return rturn;
}*/


public String GETALLcontactsBMP(String uname) {
	
Cursor c = getReadableDatabase().rawQuery("SELECT _id, profimg FROM allcontactsTable WHERE contactname =?",new String[]{uname});
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




public String getLastMsgList() {
Cursor c = getReadableDatabase().rawQuery("SELECT * FROM msgListTable ORDER BY _id ASC ",null);
String rturn=null;
if (c != null)  
{
if(c.moveToFirst())
{
	rturn = String.valueOf(c.getCount());
}
}
return rturn;
}



public Cursor getMsgList() {
return(getReadableDatabase()
.rawQuery("SELECT _id, username, msgbody, status, mid, stamp, uid FROM msgListTable ORDER BY _id ASC",
null));
}

public String getMsgListUsername(Cursor c) {
return(c.getString(1));
}

public String getMsgListBody(Cursor c) {
return(c.getString(2));
}

public String getMsgListStatus(Cursor c) {
return(c.getString(3));
}

public String getMsgListMID(Cursor c) {
return(c.getString(4));
}


public String getMsgListSTAMP(Cursor c) {
return(c.getString(5));
}


public String getMsgListUID(Cursor c) {
return(c.getString(6));
}



public String getLastNotify() {
Cursor c = getReadableDatabase().rawQuery("SELECT * FROM notificationsTable ORDER BY _id DESC ",null);
String rturn=null;
if (c != null)  
{
if(c.moveToFirst())
{
	rturn = String.valueOf(c.getCount());
}
}
return rturn;
}





public Cursor getNotify() {
return(getReadableDatabase()
.rawQuery("SELECT _id, uid, body, stamp, type, username, data FROM notificationsTable ORDER BY _id ASC",
null));
}


public String getNotifyUID(Cursor c) {
return(c.getString(1));
}


public String getNotifyBody(Cursor c) {
return(c.getString(2));
}

public String getNotifyDate(Cursor c) {
return(c.getString(3));
}

public String getNotifyType(Cursor c) {
return(c.getString(4));
}

public String getNotifyName(Cursor c) {
return(c.getString(5));
}

public String getNotifyData(Cursor c) {
return(c.getString(6));
}




public void updateHash_isExpanded(String pid,String isExpanded)
{
	ContentValues cv=new ContentValues();
	cv.put("isExpanded", isExpanded);
	getWritableDatabase().update("hashTable", cv, "postid="+pid, null);
}





public void updateHashRate(String pid,String rate,String ratecount)
{
	ContentValues cv=new ContentValues();
	cv.put("rating", rate);
	cv.put("ratecount", ratecount);
	getWritableDatabase().update("hashTable", cv, "postid="+pid, null);
}

public void updateHashComment(String pid,String commentcount)
{
	ContentValues cv=new ContentValues();
	cv.put("commentcount", commentcount);
	getWritableDatabase().update("hashTable", cv, "postid="+pid, null);
}

public void DeleteHash(String pid) {
getReadableDatabase()
.delete("hashTable", "postid="+pid,null);
}



public String getLastHash() {
Cursor c = getReadableDatabase().rawQuery("SELECT * FROM hashTable ORDER BY _id DESC ",null);
String rturn=null;
if (c != null)  
{
if(c.moveToFirst())
{
	rturn = String.valueOf(c.getCount());
}
}
return rturn;
}




public Cursor getAllHash() {
return(getReadableDatabase()
.rawQuery("SELECT _id, postid, userid, name, body, pimage, date, type, rating, ratecount, commentcount, isExpanded FROM hashTable ORDER BY _id ASC",
null));
}


public String getHashPID(Cursor c) {
return(c.getString(1));
}

public String getHashUID(Cursor c) {
return(c.getString(2));
}

public String getHashName(Cursor c) {
return(c.getString(3));
}

public String getHashBody(Cursor c) {
return(c.getString(4));
}


public byte[] getHashBmp(Cursor c) {
return(c.getBlob(5));
}

public String getHashDate(Cursor c) {
return(c.getString(6));
}

public String getHashType(Cursor c) {
return(c.getString(7));
}

public String getHashRating(Cursor c) {
return(c.getString(8));
}

   
public String getHashRateCount(Cursor c) {
return(c.getString(9));
}


public String getHashCommentCount(Cursor c) {
return(c.getString(10));
}


public String getHash_isExpanded(Cursor c) {
return(c.getString(11));
}




public void updateMAIN(String contact,String isfollow)
{
	ContentValues cv=new ContentValues();
	cv.put("following", isfollow);
	getWritableDatabase().update("maincontactsTable", cv, "contactname="+"'"+contact+"'", null);
}





public Cursor getMAINcontacts() {
return(getReadableDatabase()
.rawQuery("SELECT _id, contactname, detail, userid, following FROM maincontactsTable ORDER BY contactname",
null));
}

public String getMAINcontactName(Cursor c) {
return(c.getString(1));
}

public String getMAINcontactDetail(Cursor c) {
return(c.getString(2));
}


public String getMAINcontactUID(Cursor c) {
return(c.getString(3));
}

public String getMAINFollowing(Cursor c) {
return(c.getString(4));
}


public Cursor getMAINCONTACTUSER() {
return(getReadableDatabase()
.rawQuery("SELECT _id, contactname FROM allcontactsTable ORDER BY _id ASC",
null));
}


/*public String GETALLcontactsName(String uname) {
	
Cursor c = getReadableDatabase().rawQuery("SELECT _id, IMG FROM allcontactsTable WHERE contactname =?",new String[]{uname});
String rturn=null;
if (c != null)
{
if(c.moveToFirst())
{
	rturn=(c.getString(1));
}
}
return rturn;
}*/


public String GETMAINcontactsBMP(String uname) {
	
Cursor c = getReadableDatabase().rawQuery("SELECT _id, profimg FROM allcontactsTable WHERE contactname =?",new String[]{uname});
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


public Cursor getAllTrendingHash() {
return(getReadableDatabase()
.rawQuery("SELECT _id, hashtag, count FROM trendinghashTable ORDER BY count DESC",
null));
}

public String getTrendingHash(Cursor c) {
return(c.getString(1));
}


public String getTrendingCount(Cursor c) {
return(c.getString(2));
}


public Cursor getAllSearch() {
return(getReadableDatabase()
.rawQuery("SELECT _id, resulttitle, resultdetail, userid FROM searchTable ORDER BY resulttitle ASC",
null));
}

public String getSearchTitle(Cursor c) {
return(c.getString(1));
}


public String getSearchDetail(Cursor c) {
return(c.getString(2));
}


public String getSearchUID(Cursor c) {
return(c.getString(3));
} 


public String getSearchUID(String title) {
	
Cursor c = getReadableDatabase().rawQuery("SELECT _id, userid FROM searchTable WHERE resulttitle =?",new String[]{title});
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



}