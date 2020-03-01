package com.chaitanyav.anywherenotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "dbNotes";
    public static final int DB_VERSION = 1;
    private static final String TABLE_NOTES = "notes";
    private static final String CREATE_QUERY = "create table " + TABLE_NOTES + "(id integer primary key AUTOINCREMENT,title varchar(100) not null, data TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //no need yet
    }

    public void addNote(String noteName) {
        ContentValues content = new ContentValues();
        content.put("title", noteName);
        getWritableDatabase().insert(TABLE_NOTES, null, content);
    }

    public void removeNote(Note note) {
        getWritableDatabase().delete(TABLE_NOTES, "id=?", new String[]{"" + note.getId()});
    }

    public ArrayList<Note> getNoteList() {
        ArrayList<Note> ret = new ArrayList<>();
        Cursor c = getReadableDatabase().query(TABLE_NOTES, null, null, null, null, null, "id");
        while (c.moveToNext()) {
            ret.add(new Note(c.getString(1), c.getInt(0)));
        }
        return ret;
    }

    public String getNoteText(Note note){
        String text="";
        Cursor c = getReadableDatabase().query(TABLE_NOTES,new String[]{"data"},"id=?",new String[]{""+note.getId()},null,null,"id");
        while(c.moveToNext()){
            text=c.getString(0);
        }
        return text;
    }

    public void setNoteText(Note note,String text){
        ContentValues content = new ContentValues();
        content.put("data",text);
        getReadableDatabase().update(TABLE_NOTES,content,"id=?",new String[]{String.valueOf(note.getId())});
    }

    public void modifyNoteName(Note note, String newName){
        ContentValues content = new ContentValues();
        content.put("title",newName);
        getReadableDatabase().update(TABLE_NOTES,content,"id=?",new String[]{String.valueOf(note.getId())});
    }
}