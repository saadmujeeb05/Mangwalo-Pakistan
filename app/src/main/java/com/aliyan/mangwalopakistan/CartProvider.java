package com.aliyan.mangwalopakistan;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aliyan on 5/1/2017.
 */
public class CartProvider extends ContentProvider {

    static final String PROVIDER_NAME ="com.aliyan.mangwalopakistan.CartProvider";
    static final String URL =  "content://" + PROVIDER_NAME + "/cart";
    static  final Uri CONTENT_URL = Uri.parse(URL);
    static final int UriCode = 1;
    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME,"cart",UriCode);
    }

    private SQLiteDatabase sqlDb;
    static final String DATABASE_NAME = "ShoppingCart.db";
    static final String TABLE_NAME = "cart_table";
    static final int DATABASE_VERSION = 1;
    public static final String COL_2 = "Name";
    public static final String COL_1 = "User";
    public static final String COL_3 = "Price";
    public static final String COL_4 = "Category";
    public static final String COL_5 = "ImageRef";
    static final String CREATE_DB_TABLE = "create table " + TABLE_NAME +" (" + COL_1 + " TEXT," + COL_2 + " TEXT," + COL_3 + " LONG," + COL_4 + " TEXT,"+COL_5+" LONG, PRIMARY KEY("+COL_1+","+COL_2+"))";

    static final String Name = "Name";
    static final String User = "User";
    static final String Price = "Price";
    static final String Category = "Category";
    static final String ImageRef = "ImageRef";


    private static HashMap<String,String> values;

    @Override
    public boolean onCreate() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        sqlDb = dbHelper.getWritableDatabase();
        return (sqlDb != null);
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE_NAME);

        switch(uriMatcher.match(uri)){
            case UriCode:
                queryBuilder.setProjectionMap(values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        Cursor cursor = queryBuilder.query(sqlDb,strings,s,strings1,null,null,s1);
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case UriCode:
                return "vnd.android.cursor.dir/cart";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long rowID = sqlDb.insert(TABLE_NAME, null, contentValues);

        // Verify a row has been added
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URL, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        Toast.makeText(getContext(), "Row Insert Failed", Toast.LENGTH_LONG).show();
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        int rowsDeleted = 0;

        // Used to match uris with Content Providers
                switch (uriMatcher.match(uri))
                {
                    case UriCode:
                        rowsDeleted = sqlDb.delete(TABLE_NAME, s, strings);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown URI " + uri);
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        int rowsUpdated = 0;

        switch (uriMatcher.match(uri)) {
            case UriCode:
                rowsUpdated = sqlDb.update(TABLE_NAME, contentValues, s, strings);                 break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
