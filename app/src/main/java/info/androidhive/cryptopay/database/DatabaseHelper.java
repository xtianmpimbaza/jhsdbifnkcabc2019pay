package info.androidhive.cryptopay.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.cryptopay.database.model.Keys;
import info.androidhive.cryptopay.database.model.Note;


public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "binusu_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(Note.CREATE_TABLE);
        db.execSQL(Keys.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Note.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Keys.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertNote(String note) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Note.COLUMN_AMOUNT, note);

        // insert row
        long id = db.insert(Note.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public void insertTxs(JSONArray jsonArray) throws JSONException {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        int length = jsonArray.length();

        for (int i = 0; i < length; i++) {
            JSONObject session_object = jsonArray.getJSONObject(i);
            String txid = session_object.getString("txid");
            String fee = session_object.getString("fee");
            String blockIndex = session_object.getString("blockIndex");
            String time = session_object.getString("time");
            String confirmations = session_object.getString("confirmations");
            String sender = session_object.getString("sender");
            String recipient = session_object.getString("recipient");
            String paymentID = session_object.getString("paymentID");
            String amount = session_object.getString("amount");

//            SQLiteDatabase db = Note.getWritableDatabase();

            ContentValues newValues = new ContentValues();
            newValues.put(Note.COLUMN_TXID, txid);
            newValues.put(Note.COLUMN_FEE, fee);
            newValues.put(Note.COLUMN_BLOBKINDEX, blockIndex);
            newValues.put(Note.COLUMN_TIMESTAMP, time);
            newValues.put(Note.COLUMN_CONFIRMATIONS, confirmations);
            newValues.put(Note.COLUMN_SENDER, sender);
            newValues.put(Note.COLUMN_RECEIPIENT, recipient);
            newValues.put(Note.COLUMN_PAYMENTID, paymentID);
            newValues.put(Note.COLUMN_AMOUNT, amount);

            db.insertWithOnConflict(Note.TABLE_NAME, null, newValues, SQLiteDatabase.CONFLICT_REPLACE);
//            db.close();
        }


    }


    public long insertKeys(String publickey, String privatekey) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Keys.COLUMN_PUBLIC, publickey);
        values.put(Keys.COLUMN_PRIVATE, privatekey);

        // insert row
        long id = db.insert(Keys.TABLE_NAME, null, values);

        // close db connection
//        db.close();

        // return newly inserted row id
        return id;
    }

//    public Note getNote(long id) {
//        // get readable database as we are not inserting anything
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor cursor = db.query(Note.TABLE_NAME,
//                new String[]{Note.COLUMN_ID, Note.COLUMN_AMOUNT, Note.COLUMN_TIMESTAMP},
//                Note.COLUMN_ID + "=?",
//                new String[]{String.valueOf(id)}, null, null, null, null);
//
//        if (cursor != null)
//            cursor.moveToFirst();
//
//        // prepare note object
//        Note note = new Note(
//                cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)),
//                cursor.getString(cursor.getColumnIndex(Note.COLUMN_AMOUNT)),
//                cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP))),
//                cursor.getString(cursor.getColumnIndex(Note.COLUMN_FEE)));
//
//        // close the db connection
//        cursor.close();
//
//        return note;
//    }

    public Keys getKeys(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Keys.TABLE_NAME,
                new String[]{Keys.COLUMN_ID, Keys.COLUMN_PUBLIC, Keys.COLUMN_PRIVATE, Keys.COLUMN_TIMESTAMP},
                Keys.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare keys object
        Keys keys = new Keys(
                cursor.getInt(cursor.getColumnIndex(Keys.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Keys.COLUMN_PUBLIC)),
                cursor.getString(cursor.getColumnIndex(Keys.COLUMN_PRIVATE)),
                cursor.getString(cursor.getColumnIndex(Keys.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();

        return keys;
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Note.TABLE_NAME + " ORDER BY " +
                Note.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(cursor.getInt(cursor.getColumnIndex(Note.COLUMN_PAYMENTID)));
                note.setNote(cursor.getString(cursor.getColumnIndex(Note.COLUMN_AMOUNT)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)));
                note.setFee(cursor.getString(cursor.getColumnIndex(Note.COLUMN_FEE)));
                note.setConfirmations(cursor.getString(cursor.getColumnIndex(Note.COLUMN_CONFIRMATIONS)));
                note.setTxid(cursor.getString(cursor.getColumnIndex(Note.COLUMN_TXID)));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        // close db connection
//        db.close();

        // return notes list
        return notes;
    }

    public List<Keys> getAllKeys() {
        List<Keys> allkeys = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Keys.TABLE_NAME + " ORDER BY " +
                Keys.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Keys keys = new Keys();
                keys.setId(cursor.getInt(cursor.getColumnIndex(Keys.COLUMN_ID)));
                keys.setPublickey(cursor.getString(cursor.getColumnIndex(Keys.COLUMN_PUBLIC)));
                keys.setPrivatekey(cursor.getString(cursor.getColumnIndex(Keys.COLUMN_PRIVATE)));
                keys.setTimestamp(cursor.getString(cursor.getColumnIndex(Keys.COLUMN_TIMESTAMP)));

                allkeys.add(keys);
            } while (cursor.moveToNext());
        }

        // close db connection
//        db.close();

        // return keys list
        return allkeys;
    }

    public int getNotesCount() {
        String countQuery = "SELECT  * FROM " + Note.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    public int updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Note.COLUMN_AMOUNT, note.getNote());

        // updating row
        return db.update(Note.TABLE_NAME, values, Note.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
    }

    public void deleteNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Note.TABLE_NAME, Note.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
//        db.close();
    }
}
