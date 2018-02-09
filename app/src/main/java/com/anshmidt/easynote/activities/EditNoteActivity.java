package com.anshmidt.easynote.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.anshmidt.easynote.DatabaseHelper;
import com.anshmidt.easynote.Note;
import com.anshmidt.easynote.NotesListAdapter;
import com.anshmidt.easynote.R;
import com.anshmidt.easynote.SimpleDividerItemDecoration;

import java.util.List;

/**
 * Created by Ilya Anshmidt on 02.09.2017.
 */

public class EditNoteActivity extends BaseActivity {

    protected List<Note> notesList;
    protected RecyclerView rv;
    protected  NotesListAdapter adapter;
    private LinearLayoutManager llm;
    public int positionInList;
    private DatabaseHelper databaseHelper;
    private final String LOG_TAG = EditNoteActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editnote);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //notesList = getNotesList();
        databaseHelper = DatabaseHelper.getInstance(EditNoteActivity.this);
        rv = (RecyclerView) findViewById(R.id.recyclerView);
        llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.addItemDecoration(new SimpleDividerItemDecoration(this));

        //notesList = NoteDataHolder.getInstance().getNotesList();
        notesList = databaseHelper.getAllNotes();
        adapter = new NotesListAdapter(notesList, this);
        setNotesListAdapter(adapter);
        rv.setAdapter(adapter);

        positionInList = getIntent().getIntExtra("itemPosition", 0);

        searchRequest = getIntent().getStringExtra("searchRequest"); //or null if it's not in intent
        if (searchRequest != null) {
            Log.d(LOG_TAG, "searchRequest " + searchRequest + " was found in the intent");
        }

        llm.setStackFromEnd(true);  //to fix issue with 3 last items covered with a keyboard
    }


    @Override
    protected void onResume() {
        super.onResume();

        adapter.setSelectedItem(positionInList);
        //llm.scrollToPositionWithOffset(positionInList, 0);   //also works, but a bit different
        llm.scrollToPosition(positionInList);

    }




}
