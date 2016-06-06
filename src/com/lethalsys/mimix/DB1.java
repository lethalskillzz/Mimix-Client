package com.lethalsys.mimix;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


class Database_One extends SQLiteOpenHelper {
private static final String DATABASE_NAME="mimix_db1.db";
private static final int SCHEMA_VERSION=1;
public Database_One(Context context) {
super(context, DATABASE_NAME, null, SCHEMA_VERSION);
}
@Override
public void onCreate(SQLiteDatabase db) {
	db.execSQL("CREATE TABLE posts (_id INTEGER PRIMARY KEY AUTOINCREMENT,postid TEXT, userid TEXT, name TEXT, body TEXT, pimage BLOB, date TEXT, type TEXT, isLike TEXT,likecount TEXT,commentcount TEXT, isExpanded TEXT);");
	db.execSQL("CREATE TABLE msgs (_id INTEGER PRIMARY KEY AUTOINCREMENT, mid INTEGER, uid TEXT, tid TEXT, msgbody TEXT, date TEXT, status TEXT, who TEXT);");
	db.execSQL("CREATE TABLE add_phn (_id INTEGER PRIMARY KEY AUTOINCREMENT,contact TEXT, phone TEXT, username TEXT, img BLOB, isAdd TEXT);");
	db.execSQL("CREATE TABLE addonTable (_id INTEGER PRIMARY KEY AUTOINCREMENT, isInstalled TEXT, category TEXT, addon_package TEXT, addon TEXT, img BLOB, url TEXT);");
	db.execSQL("CREATE TABLE profileposts (_id INTEGER PRIMARY KEY AUTOINCREMENT,postid TEXT, userid TEXT, name TEXT, body TEXT, pimage BLOB, date TEXT, type TEXT, rating TEXT,ratecount TEXT,commentcount TEXT, isExpanded TEXT);");
}



@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	db.execSQL("DROP TABLE IF EXISTS posts");
	db.execSQL("DROP TABLE IF EXISTS msgs");
	db.execSQL("DROP TABLE IF EXISTS add_phn");
	db.execSQL("DROP TABLE IF EXISTS addonTable");
	db.execSQL("DROP TABLE IF EXISTS profileposts");

	onCreate(db);
}



public void Clear() {
	getWritableDatabase().execSQL("DELETE FROM "+"posts"+";");
}


public void ClearMsg() {
	getWritableDatabase().execSQL("DELETE FROM "+"msgs"+";");
}



public void ClearAddPhn() {   
	getWritableDatabase().execSQL("DELETE FROM "+"add_phn"+";");
}


public void ClearAddon() {   
	getWritableDatabase().execSQL("DELETE FROM "+"addonTable"+";");
}



public void ClearProfpost() {
	getWritableDatabase().execSQL("DELETE FROM "+"profileposts"+";");
}



public void insert(String postid, String userid, String name, String body,
byte[] pimage, String date, String type, String isLike, String likecount, String commentcount) {
ContentValues cv=new ContentValues();
cv.put("postid", postid);
cv.put("userid", userid);
cv.put("name", name);
cv.put("body", body);
cv.put("pimage", pimage);
cv.put("date", date);
cv.put("type", type);
cv.put("isLike", isLike);
cv.put("likecount", likecount);
cv.put("commentcount", commentcount);
cv.put("isExpanded", "NO");
getWritableDatabase().insert("posts", "postid", cv);
}



public void insertMsg(int mid, String uid, String tid, String msgbody, String date,
String status,	String who) {
ContentValues cv=new ContentValues();
cv.put("mid", mid);
cv.put("uid", uid);
cv.put("tid", tid);
cv.put("msgbody", msgbody);
cv.put("date", date);
cv.put("status", status);
cv.put("who", who);
getWritableDatabase().insert("msgs", "mid", cv);
}



public void insertAddPhn(String contact, String phone, String username, byte[] img, String isAdd) {
ContentValues cv=new ContentValues();
cv.put("contact", contact);
cv.put("phone", phone);
cv.put("username", username);
cv.put("img", img);
cv.put("isAdd", isAdd);
getWritableDatabase().insert("add_phn", "contact", cv);
}



public void insertAddon(String isInstalled, String category, String addon_package, String addon, byte[] img, String url) {
ContentValues cv=new ContentValues();
cv.put("isInstalled", isInstalled);
cv.put("category", category);
cv.put("addon_package", addon_package);
cv.put("addon", addon);
cv.put("img", img);
cv.put("url", url);
getWritableDatabase().insert("addonTable", "isInstalled", cv);
}




public void insertProfpost(String postid, String userid, String name, String body,
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
getWritableDatabase().insert("profileposts", "postid", cv);
}





public void updateRate(String pid,String rate,String ratecount)
{
	ContentValues cv=new ContentValues();
	cv.put("rating", rate);
	cv.put("ratecount", ratecount);
	getWritableDatabase().update("posts", cv, "postid="+pid, null);
}





public void update_isExpanded(String pid,String isExpanded)
{
	ContentValues cv=new ContentValues();
	cv.put("isExpanded", isExpanded);
	getWritableDatabase().update("posts", cv, "postid="+pid, null);
}



public void updateComment(String pid,String commentcount)
{
	ContentValues cv=new ContentValues();
	cv.put("commentcount", commentcount);
	getWritableDatabase().update("posts", cv, "postid="+pid, null);
}



public void Delete(String pid) {
getReadableDatabase()
.delete("posts", "postid="+pid,null);
}




public String getLastPost() {
Cursor c = getReadableDatabase().rawQuery("SELECT * FROM posts ORDER BY _id DESC ",null);
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





public Cursor getAll() {
return(getReadableDatabase()
.rawQuery("SELECT _id, postid, userid, name, body, pimage, date, type, isLike, likecount, commentcount, isExpanded FROM posts ORDER BY _id ASC",
null));
}


public String getPID(Cursor c) {
return(c.getString(1));
}

public String getUID(Cursor c) {
return(c.getString(2));
}

public String getName(Cursor c) {
return(c.getString(3));
}

public String getBody(Cursor c) {
return(c.getString(4));
}

public byte[] getBmp(Cursor c) {
return(c.getBlob(5));
}

public String getDate(Cursor c) {
return(c.getString(6));
}

public String getType(Cursor c) {
return(c.getString(7));
}

public String getIsLike(Cursor c) {
return(c.getString(8));
}

public String getLikeCount(Cursor c) {
return(c.getString(9));
}

public String getCommentCount(Cursor c) {
return(c.getString(10));
}


public String get_isExpanded(Cursor c) {
return(c.getString(11));
}





public void updateMsgStatus(String mid)
{
	ContentValues cv=new ContentValues();
	cv.put("status", "read");
	getWritableDatabase().update("msgs", cv, "mid="+mid, null);
}



public Cursor getAllMsg(String uid, String tid) {
return(getReadableDatabase()
.rawQuery("SELECT _id, who, msgbody, date FROM msgs WHERE uid =? AND tid =? OR uid =? AND tid =? ORDER BY _id ASC ",new String[]{uid,tid,tid,uid}));
}


public String getWho(Cursor c) {
return(c.getString(1));
}
public String getMsgBody(Cursor c) {
return(c.getString(2));
}

public String getMsgDate(Cursor c) {
return(c.getString(3));
}

public Cursor getAllMsgX() {
return(getReadableDatabase()
.rawQuery("SELECT _id, who, msgbody, date FROM msgs ORDER BY _id ASC",
null));
}



public String getLastMsg(String uid, String tid) {
Cursor c = getReadableDatabase().rawQuery("SELECT * FROM msgs WHERE uid =? AND tid =? OR uid =? AND tid =? ORDER BY _id DESC ",new String[]{uid,tid,tid,uid});
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





public Cursor getAllAddPhn() {
return(getReadableDatabase()
.rawQuery("SELECT _id, contact, phone, username, isAdd  FROM add_phn ORDER BY _id ASC",
null));
}


public String getAddPhnContact (Cursor c) {
return(c.getString(1));
}

public String getAddPhnPhone (Cursor c) {
return(c.getString(2));
}


public String getAddPhnUser (Cursor c) {
return(c.getString(3));
}


public String getAddPhnIsAdd (Cursor c) {
return(c.getString(4));
}

/*public  byte[] getAddPhnImg(Cursor c) {
return(c.getBlob(3));
}*/

public byte[] getAddPhnImg(String usr) {

Cursor c = getReadableDatabase().rawQuery("SELECT _id, img FROM add_phn WHERE username =? ",new String[]{usr});
byte[] rturn=null;
if (c != null)
{
if(c.moveToFirst())
{
	rturn=(c.getBlob(1));
}
}

return rturn;

}




public void updateAddPhn(String usr, String isAdd)
{
ContentValues cv=new ContentValues();
cv.put("isAdd", isAdd);
getWritableDatabase().update("add_phn", cv, "username="+"'"+usr+"'", null);
}


public Cursor getAllSelectedAddPhn() {

Cursor c = getReadableDatabase().rawQuery("SELECT _id, username FROM add_phn WHERE isAdd =? ",new String[]{"Y"});
return c;

}




public void Delete_Addon(String isInstalled) {
getReadableDatabase()
.delete("addonTable", "isInstalled="+isInstalled,null);
}



public Cursor getAllAddon() {
return(getReadableDatabase()
.rawQuery("SELECT _id, category, addon_package FROM addonTable WHERE isInstalled =? ",new String[]{"NO"}));
}



public Cursor getAll_I_Addon() {
return(getReadableDatabase()
.rawQuery("SELECT _id, category, addon_package FROM addonTable WHERE isInstalled =? ",new String[]{"YES"}));
}







public String get_Addon_txt(String addon_package) {
Cursor c = getReadableDatabase().rawQuery("SELECT _id, addon FROM addonTable WHERE addon_package =? ",new String[]{addon_package});
String rturn=null;
if (c != null)  
{
if(c.moveToFirst())
{
	rturn = c.getString(1);
}
}
return rturn;
}




public byte[] get_Addon_img(String addon_package) {
Cursor c = getReadableDatabase().rawQuery("SELECT _id, img FROM addonTable WHERE addon_package =? ",new String[]{addon_package});
byte[] rturn=null;
if (c != null)
{
if(c.moveToFirst())
{
	rturn=(c.getBlob(1));
}
}
return rturn;
}





public String get_Addon_isInstalled(String addon_package) {
Cursor c = getReadableDatabase().rawQuery("SELECT _id, isInstalled FROM addonTable WHERE addon_package =? ",new String[]{addon_package});
String rturn=null;
if (c != null)  
{
if(c.moveToFirst())
{
	rturn = c.getString(1);
}
}
return rturn;
}





public void updateProfpost_isExpanded(String pid,String isExpanded)
{
	ContentValues cv=new ContentValues();
	cv.put("isExpanded", isExpanded);
	getWritableDatabase().update("profileposts", cv, "postid="+pid, null);
}


public void updateProfpostRate(String pid,String rate,String ratecount)
{
	ContentValues cv=new ContentValues();
	cv.put("rating", rate);
	cv.put("ratecount", ratecount);
	getWritableDatabase().update("profileposts", cv, "postid="+pid, null);
}




public void updateProfpostComment(String pid,String commentcount)
{
	ContentValues cv=new ContentValues();
	cv.put("commentcount", commentcount);
	getWritableDatabase().update("profileposts", cv, "postid="+pid, null);
}



public void DeleteProfpost(String pid) {
getReadableDatabase()
.delete("profileposts", "postid="+pid,null);
}




public String getLastProfpost() {
Cursor c = getReadableDatabase().rawQuery("SELECT * FROM profileposts ORDER BY _id DESC ",null);
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





public Cursor getAllProfpost() {
return(getReadableDatabase()
.rawQuery("SELECT _id, postid, userid, name, body, pimage, date, type, rating, ratecount, commentcount, isExpanded FROM profileposts ORDER BY _id ASC",
null));
}


public String getProfpostPID(Cursor c) {
return(c.getString(1));
}

public String getProfpostUID(Cursor c) {
return(c.getString(2));
}

public String getProfpostName(Cursor c) {
return(c.getString(3));
}

public String getProfpostBody(Cursor c) {
return(c.getString(4));
}

public byte[] getProfpostBmp(Cursor c) {
return(c.getBlob(5));
}

public String getProfpostDate(Cursor c) {
return(c.getString(6));
}

public String getProfpostType(Cursor c) {
return(c.getString(7));
}

public String getProfpostRating(Cursor c) {
return(c.getString(8));
}

public String getProfpostRateCount(Cursor c) {
return(c.getString(9));
}

public String getProfpostCommentCount(Cursor c) {
return(c.getString(10));
}

public String getProfpost_isExpanded(Cursor c) {
return(c.getString(11));
}



}