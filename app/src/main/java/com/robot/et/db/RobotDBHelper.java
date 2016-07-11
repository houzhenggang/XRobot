package com.robot.et.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class RobotDBHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "robot.db";
	private static final int DB_VERSION = 1;

	public RobotDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
	
	public RobotDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//提醒表
		String reminds = "create table reminds"+
				"("+ 
				"id integer primary key autoincrement,robotNum varchar(50),date varchar(50),time varchar(20),content varchar(200),remindInt integer," +
				"frequency integer,originalAlarmTime varchar(50),remindMen varchar(50),requireAnswer varchar(200),spareContent varchar(200),spareType integer"+
				 ")";
		//用户电话联系表
		String users = "create table users"+
				"("+ 
				"id integer primary key autoincrement,userId integer,userName varchar(64),userPhone varchar(20)"+
				")";
		//智能学习问题表
		String question = "create table question"+
				"("+ 
				"id integer primary key autoincrement,robotNum varchar(30),question varchar(2000),learnType integer"+
				")";
		//智能学习答案表
		String answer = "create table answer"+
				"("+ 
				"id integer primary key autoincrement,questionId integer,robotNum varchar(30),answer varchar(2000),action varchar(100),learnType integer"+
				")";
		//剧本表
		String script = "create table script"+
				"("+
				"id integer primary key autoincrement,userPhone varchar(20),robotNum varchar(30),scriptContent varchar(100),scriptType integer,remarks varchar(500)"+
				")";
		//剧本动作执行表
		String scriptAction = "create table scriptAction"+
				"("+
				"id integer primary key autoincrement,scriptId integer,actionType integer,content varchar(500),spareContent varchar(500),direction integer," +
				"remarks varchar(500)"+
				")";

		db.execSQL(reminds);
		db.execSQL(users);
		db.execSQL(question);
		db.execSQL(answer);
		db.execSQL(script);
		db.execSQL(scriptAction);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
