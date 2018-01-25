package com.aliyan.mangwalopakistan;

/**
 * Created by Aliyan on 4/29/2017.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "ShoppingCart.db";
    public static final String TABLE_NAME = "cart_table";
    public static final String COL_2 = "Name";
    public static final String COL_1 = "User";
    public static final String COL_3 = "Price";
    public static final String COL_4 = "Category";
    public static final String COL_5 = "ImageRef";


    public DatabaseHelper(Context context) {
        super(context,DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (" + COL_1 + " TEXT," + COL_2 + " TEXT," + COL_3 + " LONG," + COL_4 + " TEXT,"+COL_5+" LONG, PRIMARY KEY("+COL_1+","+COL_2+"))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String user,String name, Long price, String category, Long image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,user);
        contentValues.put(COL_2,name);
        contentValues.put(COL_3,price);
        contentValues.put(COL_4,category);
        contentValues.put(COL_5,image);
        long result = db.insert(TABLE_NAME,null,contentValues);
        return (result != -1);
    }

    public Cursor getAllData(String userID) {
        SQLiteDatabase db = this.getWritableDatabase();
            Cursor res = db.query(true,TABLE_NAME,new String[]{COL_1,COL_2,COL_3,COL_4,COL_5},COL_1 + " = ?",new String[]{userID},null,null,null,null);
            //Cursor res = db.rawQuery("Select * from " + TABLE_NAME + " where " + COL_1 + " = " + userID, null);
            return res;
    }

    public boolean updateData(String id,String name,String surname,String marks) {
        return true;
    }

    public Integer deleteData (String uid,String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COL_2 + "= ? and " + COL_1 +"=?",new String[]{name,uid});
    }

    public boolean checkForCart(String uid, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from " + TABLE_NAME + " where " + COL_1 + "= ? and " + COL_2 + "= ?",new String[]{uid,name});
        return (res.getCount() != 0);
    }
}
