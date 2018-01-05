package com.example.emyeraky.simplegithub;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Emy Eraky on 1/5/2018.
 */

public class DBContoller {
    DBHelper dbHelper;
    SQLiteDatabase database;

    public DBContoller(Context c) {
        dbHelper = new DBHelper(c);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close_db() {
        dbHelper.close();
    }

    public int insert_db(String repo, String username, String desp,String html_url) {
        open();

        ContentValues values = new ContentValues();
        values.put(DBHelper.RepoName, repo);
        values.put(DBHelper.UserName, username);
        values.put(DBHelper.Desp, desp);
        values.put(DBHelper.Html_Url, html_url);


        int num = (int) database.insert(DBHelper.TABLE_NAME, null, values);

        close_db();
        return num;
    }

    public Cursor get_dataselect() {

        open();
        String[] column = { DBHelper.RepoName, DBHelper.UserName, DBHelper.Desp,DBHelper.Html_Url};
        Cursor c = database.query(DBHelper.TABLE_NAME, column, null, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }
        close_db();
        return c;
    }
}
