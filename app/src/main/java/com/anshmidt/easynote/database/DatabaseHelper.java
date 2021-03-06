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
        this.context = context;
        this.db = this.getWritableDatabase();  //so db is opened only once

        notesDao = new NotesDao(this.db);
        listsDao = new ListsDao(this.db);
        priorityDao = new PriorityDao(this.db);
    }

    public static synchronized DatabaseHelper getInstance(Context context){
        if (databaseHelperInstance == null) {
            databaseHelperInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return databaseHelperInstance;
//        if (databaseHelperInstance == null) {
//            synchronized (DatabaseHelper.class) {
//                if (databaseHelperInstance == null) {
//                    databaseHelperInstance = new DatabaseHelper(context);
//                }
//            }
//        }
//        return databaseHelperInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {  // is only run when the database file did not exist and was just created
        notesDao = new NotesDao(db);
        listsDao = new ListsDao(db);
        priorityDao = new PriorityDao(db);

        notesDao.onCreate(db);
        listsDao.onCreate(db);
        priorityDao.onCreate(db);
        fillDatabaseWithDefaultData(db);
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

    public void fillDatabaseWithDefaultData(SQLiteDatabase db) {
        Log.d(LOG_TAG, "Filling DB with default data");
        notesDao.fillWithDefaultData(db, context);
        listsDao.fillWithDefaultData(db, context);
        priorityDao.fillWithDefaultData(db, context);
    }

    public int addNote(Note note) {
        return notesDao.addNote(note);
    }

    public void addList(NotesList notesList) {
        listsDao.addList(notesList);
    }

    public void performSqlRequest() {  //for debugging
        Log.d(LOG_TAG, "getPriorityIdByName(\"Important\") = " + priorityDao.getPriorityIdByName("Important"));
    }

//    public ArrayList<Note> getAllNotes() {
//        return notesDao.getAllNotes();
//    }

    public ArrayList<Note> getAllNotesFromList(NotesList list) {
        return notesDao.getAllNotesFromList(list);
    }

    public ArrayList<Note> getAllNotesFromTrash() {
        return notesDao.getAllNotesFromTrash();
    }

    public ArrayList<Note> getSearchResultsFromAllLists(String searchRequest) {  //excluding Trash
        return notesDao.getSearchResults(searchRequest, false);
    }

    public ArrayList<Note> getSearchResultsFromTrash(String searchRequest) {
        return notesDao.getSearchResults(searchRequest, true);
    }

    public void printAllNotes() {
        notesDao.getAllNotes();
    }

    public void deleteNote(Note note) {
        notesDao.deleteNote(note);
    }

    public void moveNoteToTrash(Note note) {
        notesDao.moveNoteToTrash(note);
    }

    public void moveListToTrash(NotesList list) {
        listsDao.moveListToTrash(list);
    }

    public void moveNoteToAnotherList(Note noteToMove, NotesList destinationList) {
        notesDao.moveNoteToAnotherList(noteToMove, destinationList);
    }

    public void moveAllNotesFromListToTrash(NotesList list) {
        ArrayList<Note> allNotesFromList = getAllNotesFromList(list);
        for (int i = 0; i<allNotesFromList.size(); i++) {
            moveNoteToTrash(allNotesFromList.get(i));
        }
    }

    public void updateNote(Note note) {
        notesDao.updateNote(note);
    }

    public int getEmptyNotesCountInList(NotesList list) {
        return notesDao.getEmptyNotesCountInList(list);
    }

    public void deleteEmptyNotesFromList(NotesList list) {
        notesDao.deleteEmptyNotesFromList(list);
    }

    public void deleteTrashNotes() {
        notesDao.deleteTrashNotes();
    }

    public void deleteTrashLists() {
        listsDao.deleteTrashLists();
    }

    public List<String> getAllListNames() {
        return listsDao.getAllListNamesNotFromTrash();
    }



    public String getListNameById(int listId) {
        return listsDao.getListById(listId).name;
    }

    public int getListIdByName(String listName) {
        return listsDao.getListIdByName(listName);
    }

    public NotesList getListById(int listId) {
        return listsDao.getListById(listId);
    }

    public void updateList(NotesList list) {
        listsDao.updateList(list);
    }

    public void deleteAllEmptyNotes() {
        notesDao.deleteAllEmptyNotes();
    }

}
