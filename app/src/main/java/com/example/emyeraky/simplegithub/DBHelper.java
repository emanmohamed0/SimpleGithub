package com.example.emyeraky.simplegithub;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Emy Eraky on 1/5/2018.
 */

public class DBHelper extends SQLiteOpenHelper {
    //database_declare
    private static final String DATABASE_NAME="Github";
    private static final int DATABASE_VERSION=3;

    //tables
    public static final String TABLE_NAME="GitData";

    //columns
    public static final String ID="_id";
    public static final String RepoName="reponame";
    public static final String Desp = "desp" ;
    public static final String UserName = "username" ;
    public static final String Html_Url ="html_url" ;


    //create TABLE
    String CREATE_TABLE="CREATE TABLE "+TABLE_NAME+
            "("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
            RepoName+" TEXT,"+UserName+" ,"+Desp+" TEXT , "+Html_Url+" TEXT)";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null , DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
