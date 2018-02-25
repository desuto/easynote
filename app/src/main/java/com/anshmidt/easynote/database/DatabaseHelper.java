package com.anshmidt.easynote.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.anshmidt.easynote.Note;
import com.anshmidt.easynote.NotesList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilya Anshmidt on 21.02.2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "notes";
    private SQLiteDatabase db;

    private final String LOG_TAG = DatabaseHelper.class.getSimpleName();
    private static DatabaseHelper databaseHelperInstance;
    private Context context;

    private NotesDao notesDao;
    private ListsDao listsDao;
    private PriorityDao priorityDao;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.db = this.getWritableDatabase();  //so db is opened only once
        this.context = context;
        notesDao = new NotesDao(this.db);
        listsDao = new ListsDao(this.db);
        priorityDao = new PriorityDao(this.db);
    }

    public static DatabaseHelper getInstance(Context context){
        if (databaseHelperInstance == null) {
            synchronized (DatabaseHelper.class) {
                if (databaseHelperInstance == null) {
                    databaseHelperInstance = new DatabaseHelper(context);
                }
            }
        }
        return databaseHelperInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {  // is only run when the database file did not exist and was just created
        notesDao = new NotesDao(db);
        listsDao = new ListsDao(db);
        priorityDao = new PriorityDao(db);

        notesDao.onCreate(db);
        listsDao.onCreate(db);
        priorityDao.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  // is only called when the database file exists but the stored version number is lower than requested
        notesDao = new NotesDao(db);
        listsDao = new ListsDao(db);
        priorityDao = new PriorityDao(db);

        notesDao.onUpgrade(db);
        listsDao.onUpgrade(db);
        priorityDao.onUpgrade(db);
    }

    public void fillDatabaseWithDefaultData() {
        notesDao.fillWithDefaultData(db, context);
        listsDao.fillWithDefaultData(db, context);
        priorityDao.fillWithDefaultData(db, context);
    }

    public void addNote(Note note) {
        notesDao.addNote(note);
    }

    public void performSqlRequest() {  //for debugging
        Log.d(LOG_TAG, "getPriorityIdByName(\"Important\") = " + priorityDao.getPriorityIdByName("Important"));
    }

    public ArrayList<Note> getAllNotes() {
        return notesDao.getAllNotes();
    }

    public void printAllNotes() {
        notesDao.getAllNotes();
    }

    public void deleteNote(Note note) {
        notesDao.deleteNote(note);
    }

    public void updateNote(Note note) {
        notesDao.updateNote(note);
    }

    public int getEmptyNotesCount() {
        return notesDao.getEmptyNotesCount();
    }

    public void deleteEmptyNotes() {
        notesDao.deleteEmptyNotes();
    }

    public List<String> getAllListNames() {
        return listsDao.getAllListNames();
    }


}
