package com.example.attendancemanagementsystemapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME =
            "attendance.db";

    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {

        super(
                context,
                DATABASE_NAME,
                null,
                DATABASE_VERSION
        );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE persons (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT," +
                        "mobile TEXT)"
        );

        db.execSQL(
                "CREATE TABLE attendance (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "person_id INTEGER," +
                        "date TEXT," +
                        "status TEXT)"
        );
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        db.execSQL(
                "DROP TABLE IF EXISTS attendance"
        );

        db.execSQL(
                "DROP TABLE IF EXISTS persons"
        );

        onCreate(db);
    }

    public boolean addPerson(
            String name,
            String mobile) {

        SQLiteDatabase db =
                getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put("name", name);
        values.put("mobile", mobile);

        long result =
                db.insert(
                        "persons",
                        null,
                        values
                );

        return result != -1;
    }

    public Cursor getPersons() {

        SQLiteDatabase db =
                getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM persons ORDER BY id DESC",
                null
        );
    }

    public boolean deletePerson(int personId) {

        SQLiteDatabase db =
                getWritableDatabase();

        db.delete(
                "attendance",
                "person_id=?",
                new String[]{
                        String.valueOf(personId)
                }
        );

        int result =
                db.delete(
                        "persons",
                        "id=?",
                        new String[]{
                                String.valueOf(personId)
                        }
                );

        return result > 0;
    }

    public void markAttendance(
            int personId,
            String date,
            String status) {

        SQLiteDatabase db =
                getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id FROM attendance " +
                        "WHERE person_id=? AND date=?",
                new String[]{
                        String.valueOf(personId),
                        date
                }
        );

        if (cursor.moveToFirst()) {

            ContentValues values =
                    new ContentValues();

            values.put("status", status);

            db.update(
                    "attendance",
                    values,
                    "person_id=? AND date=?",
                    new String[]{
                            String.valueOf(personId),
                            date
                    }
            );

        } else {

            ContentValues values =
                    new ContentValues();

            values.put("person_id", personId);
            values.put("date", date);
            values.put("status", status);

            db.insert(
                    "attendance",
                    null,
                    values
            );
        }

        cursor.close();
    }

    public String getAttendance(
            int personId,
            String date) {

        SQLiteDatabase db =
                getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT status FROM attendance " +
                                "WHERE person_id=? AND date=?",
                        new String[]{
                                String.valueOf(personId),
                                date
                        }
                );

        String status = "Not Marked";

        if (cursor.moveToFirst()) {

            status =
                    cursor.getString(0);
        }

        cursor.close();

        return status;
    }
}
