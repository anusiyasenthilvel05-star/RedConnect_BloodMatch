package com.example.redconnectlogo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "RedConnect.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE donors (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE," +
                "password TEXT," +
                "name TEXT," +
                "age TEXT," +
                "bloodGroup TEXT," +
                "phone TEXT UNIQUE," +
                "email TEXT," +
                "gender TEXT," +
                "state TEXT," +
                "district TEXT," +
                "city TEXT," +
                "pincode TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS donors");
        onCreate(db);
    }


    public boolean insertDonor(String username, String password,
                               String name, String age,
                               String bloodGroup, String phone,
                               String email, String gender,
                               String state, String district,
                               String city, String pincode) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("username", username);
        cv.put("password", password);
        cv.put("name", name);
        cv.put("age", age);
        cv.put("bloodGroup", bloodGroup);
        cv.put("phone", phone);
        cv.put("email", email);
        cv.put("gender", gender);
        cv.put("state", state);
        cv.put("district", district);
        cv.put("city", city);
        cv.put("pincode", pincode);

        long result = db.insert("donors", null, cv);
        return result != -1;
    }


    public boolean updateDonor(String username,
                               String name, String age,
                               String bloodGroup, String phone,
                               String email, String gender,
                               String state, String district,
                               String city, String pincode) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("name", name);
        cv.put("age", age);
        cv.put("bloodGroup", bloodGroup);
        cv.put("phone", phone);
        cv.put("email", email);
        cv.put("gender", gender);
        cv.put("state", state);
        cv.put("district", district);
        cv.put("city", city);
        cv.put("pincode", pincode);

        int result = db.update("donors", cv,
                "username=?", new String[]{username});

        return result > 0;
    }


    public boolean checkLogin(String username, String password) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM donors WHERE username=? AND password=?",
                new String[]{username, password});

        return cursor.getCount() > 0;
    }

    public boolean checkUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM donors WHERE username=?",
                new String[]{username});
        return cursor.getCount() > 0;
    }


    public boolean checkPhone(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM donors WHERE phone=?",
                new String[]{phone});
        return cursor.getCount() > 0;
    }


    public Cursor getUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM donors WHERE username=?",
                new String[]{username});
    }


    public void deleteUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("donors", "username=?",
                new String[]{username});
    }
}