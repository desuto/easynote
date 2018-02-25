package com.anshmidt.easynote.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.anshmidt.easynote.Note;
import com.anshmidt.easynote.NotesList;
import com.anshmidt.easynote.Priority;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilya Anshmidt on 21.02.2018.
 */

public class NotesDao implements TableHelper {
    public static final String NOTES_TABLE_NAME = "notes";
    private final String LOG_TAG = NotesDao.class.getSimpleName();

    public static final String KEY_NOTE_ID = "note_id";
    public static final String KEY_MODIFIED_AT = "modification_timestamp";
    public static final String KEY_TEXT = "text";
    public static final String KEY_IN_TRASH = "in_trash";  // SQLite doesn't have boolean, so it's int
    public static final String KEY_PRIORITY_ID = "priority_id";
    public static final String KEY_LIST_ID = "list_id";

    private SQLiteDatabase db;

    public NotesDao(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NOTES_TABLE = "CREATE TABLE IF NOT EXISTS " + NOTES_TABLE_NAME + " ("
                + KEY_NOTE_ID + " INTEGER PRIMARY KEY, "  // SQLite points this column to ROWID column
                + KEY_MODIFIED_AT + " INTEGER, "
                + KEY_TEXT + " TEXT, "
                + KEY_IN_TRASH + " INTEGER, "
                + KEY_PRIORITY_ID + " INTEGER, "
                + KEY_LIST_ID + " INTEGER)";
        db.execSQL(CREATE_NOTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db) {
        drop(db);
        onCreate(db);
    }

    @Override
    public void drop(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE_NAME);
    }

    @Override
    public void fillWithDefaultData(SQLiteDatabase db, Context context) {
        drop(db);
        onCreate(db);
        DefaultData defaultData = new DefaultData(context);
        addNotes(defaultData.getDefaultNotes());
    }

    private void addNotes(List<Note> notes) {
        for (int i = 0; i < notes.size(); i++) {
            addNote(notes.get(i));
        }
    }

    public void addNote(Note note) {
        ContentValues values = new ContentValues();
        values.put(KEY_MODIFIED_AT, note.modificationTime);
        values.put(KEY_TEXT, note.text);
        values.put(KEY_IN_TRASH, note.inTrash);

        int priorityId = note.priority.id;
        if (priorityId == 0) {  //if not initialized
            PriorityDao priorityDao = new PriorityDao(db);
            priorityId = priorityDao.getPriorityIdByName(note.priority.name);
        }
        values.put(KEY_PRIORITY_ID, priorityId);

        int listId = note.list.id;
        if (listId == 0) {
            ListsDao listsDao = new ListsDao(db);
            listId = listsDao.getListIdByName(note.list.name);
        }
        values.put(KEY_LIST_ID, note.list.id);

        db.insert(NOTES_TABLE_NAME, null, values);
        Log.d(LOG_TAG, "Note inserted: " + note.modificationTime + ", " + note.text + ", " + note.inTrash
                + ", " + note.priority.id + ", " + note.list.id);
    }

//    public Note getNote() {
//
//    }

    public ArrayList<Note> getAllNotes() {

        String selectAllNotesQuery = "SELECT "
                + KEY_NOTE_ID + ", "
                + KEY_MODIFIED_AT + ", "
                + KEY_TEXT + ", "
                + NOTES_TABLE_NAME + "." + KEY_PRIORITY_ID + ", "
                + PriorityDao.PRIORITY_TABLE_NAME + "." + PriorityDao.KEY_PRIORITY_NAME + ", "
                + NOTES_TABLE_NAME + "." + KEY_LIST_ID + ", "
                + ListsDao.LISTS_TABLE_NAME + "." + ListsDao.KEY_LIST_NAME
                + " FROM " + NOTES_TABLE_NAME
                + " LEFT OUTER JOIN " + ListsDao.LISTS_TABLE_NAME
                + " ON " + NOTES_TABLE_NAME + "." + KEY_LIST_ID + " = " + ListsDao.LISTS_TABLE_NAME + "." + KEY_LIST_ID
                + " LEFT OUTER JOIN " + PriorityDao.PRIORITY_TABLE_NAME
                + " ON " + NOTES_TABLE_NAME + "." + KEY_PRIORITY_ID + " = " + PriorityDao.PRIORITY_TABLE_NAME + "." + KEY_PRIORITY_ID
                + " ORDER BY "
                + NOTES_TABLE_NAME + "." + KEY_PRIORITY_ID + " ASC, "
                + KEY_MODIFIED_AT + " DESC";

        Cursor cursor = db.rawQuery(selectAllNotesQuery, null);
        ArrayList<Note> notesList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                int noteId = cursor.getInt(0);
                long noteModificationTime = cursor.getLong(1);
                String noteText = cursor.getString(2);

                int notePriorityId = cursor.getInt(3);
                String priorityName = cursor.getString(4);
                Priority notePriority = new Priority(notePriorityId, priorityName);

                int noteListId = cursor.getInt(5);
                String noteListName = cursor.getString(6);
                NotesList list = new NotesList(noteListId, noteListName);

                Note note = new Note(noteId, noteModificationTime, noteText, notePriority, list);

                notesList.add(note);
                Log.d(LOG_TAG, "getAllNotes(): note: ");
                note.printContentToLog();
            } while (cursor.moveToNext());
        }
        cursor.close();

        return notesList;
    }

    public void updateNote(Note note) {
        Log.d(LOG_TAG, "Updating note:");
        note.printContentToLog();

        ContentValues values = new ContentValues();
        values.put(KEY_MODIFIED_AT, note.modificationTime);
        values.put(KEY_TEXT, note.text);
        values.put(KEY_IN_TRASH, note.inTrash);

        int priorityId = note.priority.id;
        if (priorityId == 0) {  //if not initialized
            PriorityDao priorityDao = new PriorityDao(db);
            priorityId = priorityDao.getPriorityIdByName(note.priority.name);
        }
        values.put(KEY_PRIORITY_ID, priorityId);

        int listId = note.list.id;
        if (listId == 0) {
            ListsDao listsDao = new ListsDao(db);
            listId = listsDao.getListIdByName(note.list.name);
        }
        values.put(KEY_LIST_ID, listId);

        int result = db.update(NOTES_TABLE_NAME, values, KEY_NOTE_ID + " = ?",
                new String[] { String.valueOf(note.id) });
    }

    public void deleteNote(Note note) {
        db.delete(NOTES_TABLE_NAME, KEY_NOTE_ID + " = ?",
                new String[] { String.valueOf(note.id) });
        Log.d(LOG_TAG,"note was deleted from database:");
        note.printContentToLog();
    }

    public int getEmptyNotesCount() {
        String query = "SELECT COUNT(" + KEY_NOTE_ID + ") FROM " + NOTES_TABLE_NAME + " WHERE " + KEY_TEXT + " = ''";
        Cursor cursor = db.rawQuery(query, null);
        int emptyNotesCount = -1;
        if (cursor.moveToFirst()) {
            emptyNotesCount = cursor.getInt(0);
        }
        cursor.close();
        Log.d(LOG_TAG, "Empty notes count: " + emptyNotesCount);
        return emptyNotesCount;
    }

    public void deleteEmptyNotes() {
        getEmptyNotesCount();
        String query = "DELETE FROM " + NOTES_TABLE_NAME + " WHERE trim(" + KEY_TEXT + ")=''";
        db.execSQL(query);
        getEmptyNotesCount();
    }



}
