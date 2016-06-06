package com.lethalsys.mimix;


public class StringBox {
	
private String usrname="";
private String uid = null;
private String pid = null;
private String body="";
private String date="";
private byte[] postimg=null;
private String IMG="";
private String rate = null;

private String commentdate="";
private String commentuid=null;
private String comment="";
private String commentusr = null;


private String contact = null;
private String contactdetail = null;
private String contactIMG = null;
private String contactfollowing = null;


public String getUsrname() {
return(usrname);
}

public void setUsrname(String usrname) {
this.usrname=usrname;
}



public String getUid() {
return(uid);
}


public void setUid(String uid) {
this.uid=uid;
}



public String getBody() {
return(body);
}

public void setBody(String body) {
this.body=body;
}


public byte[] getPostimg() {
return(postimg);
}

public void setPostimg(byte[] postimg) {
this.postimg=postimg;
}


public String getIMG() {
return(IMG);
}

public void setIMG(String IMG) {
this.IMG=IMG;
}


public String getDate() {
return(date);
}


public void setDate(String date) {
this.date=date;
}


public String getRate() {
return(rate);
}


public void setRate(String rate) {
this.rate=rate;
}



public String getPid() {
return(pid);
}


public void setPid(String pid) {
this.pid=pid;
}



public String getcommentusr() {
return(commentusr);
}


public void setcommentusr(String commentusr) {
this.commentusr=commentusr;
}

public String getcommentuid() {
return(commentuid);
}


public void setcommentuid(String commentuid) {
this.commentuid=commentuid;
}

public String getcomment() {
return(comment);
}


public void setcomment(String comment) {
this.comment=comment;
}

public String getcommentdate() {
return(commentdate);
}


public void setcommentdate(String commentdate) {
this.commentdate=commentdate;
}



public String getcontact() {
return(contact);
}


public void setcontact(String contact) {
this.contact=contact;
}


public String getcontactdetail() {
return(contactdetail);
}


public void setcontactdetail(String detail) {
this.contactdetail=detail;
}


public String getcontactIMG() {
return(contactIMG);
}


public void setcontactIMG(String contactIMG) {
this.contactIMG=contactIMG;
}


public String getcontactfollowing() {
return(contactfollowing);
}


public void setcontactfollowing(String contactfollowing) {
this.contactfollowing=contactfollowing;
}


public String toString() {
return(getUsrname());
}



}

