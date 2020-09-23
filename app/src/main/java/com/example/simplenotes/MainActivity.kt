package com.example.simplenotes

import RecyclerAdapter
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.example.simplenotes.room.NotesDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity(), RecyclerAdapter.CellClickListener {

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var inflater = menuInflater
        inflater.inflate(R.menu.notes_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        when(item.itemId) {
            R.id.deleteAllMenu -> {
                    deleteAll()
            }
            else -> return false
        }

        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // https://developer.android.com/training/data-storage/room
        // Using Room: https://medium.com/mindorks/using-room-database-android-jetpack-675a89a0e942

        CoroutineScope(Dispatchers.Main).launch{
            startDatabase()
        }

        // Cards RecyclerView
        notesView.layoutManager = GridLayoutManager(this,2)
        notesView.adapter = RecyclerAdapter(arrayListOf(), this, this)


        // New note onclicklistener
        newNoteButton.setOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View): Unit {
                val intent = Intent(applicationContext, AddNoteScreen::class.java)
                startActivityForResult(intent, 1)
            }
        })

    }

    // https://stackoverflow.com/questions/14292398/how-to-pass-data-from-2nd-activity-to-1st-activity-when-pressed-back-android
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var returnString = data?.getStringExtra("newNote")
        var updatedString = data?.getStringExtra("updatedNote")
        var updatedStringID = data?.getIntExtra("updatedNoteID", -1)

        if (returnString != null) {
            addNote(returnString)
        }
        else if (updatedString != null){
            val note = Notes(updatedStringID, updatedString)
            updateNote(note)
        }
    }

    suspend fun startDatabase(){
        var appDB = NotesDatabase.getInstance(application)
        var result = appDB.getNotesDao().getAll()

        notesView.adapter = RecyclerAdapter(result, this, this)
    }
    
    private fun addNote(note: String) {
        CoroutineScope(Dispatchers.IO).launch {
            var appDB = NotesDatabase.getInstance(application)
            appDB.getNotesDao().insertStringNote(note)

//            var newNote = Notes(null,"This is another way to insert note")
            val newNotesList = appDB.getNotesDao().getAll()
            launch(Dispatchers.Main) {
                updateNotesView(newNotesList)
            }
        }
    }

    private fun updateNote(note: Notes) {
        CoroutineScope(Dispatchers.IO).launch {
            var appDB = NotesDatabase.getInstance(application)
            appDB.getNotesDao().updateNote(note)

//            var newNote = Notes(null,"This is another way to insert note")
            val newNotesList = appDB.getNotesDao().getAll()
            launch(Dispatchers.Main) {
                updateNotesView(newNotesList)
            }
        }
    }

    private fun deleteAll() {

        AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Are you sure you would like to delete ALL notes?")
            .setMessage("This cannot be undone.")
            .setPositiveButton("Yes", object: DialogInterface.OnClickListener {
                override fun onClick(dialogInterface: DialogInterface, i: Int) {
                    CoroutineScope(Dispatchers.Main).launch {
                        reallyDeleteAll()
                    }

                }
            })
            .setNegativeButton("No", null)
            .show()

    }

    suspend fun reallyDeleteAll() = withContext(Dispatchers.IO) {
        var appDB = NotesDatabase.getInstance(application)
        appDB.getNotesDao().nukeNotes()
        launch(Dispatchers.Main) {
            updateNotesView()
        }
    }

    suspend fun deleteNote(id: Int?) = withContext(Dispatchers.IO) {
        if (id != null) {
            var appDB = NotesDatabase.getInstance(application)
            appDB.getNotesDao().deleteNote(id)
            val result = appDB.getNotesDao().getAll()
            launch(Dispatchers.Main) {
                updateNotesView(result)
            }
        }
    }

    fun updateNotesView(listIn: List<Notes> = arrayListOf()) {
        notesView.adapter = RecyclerAdapter(listIn, this, this)
    }

    override fun onCellClickListener(selected: Notes) {
        val intent = Intent(applicationContext, EditNoteScreen::class.java)
        intent.putExtra("SavedNote", selected.noteData)
        intent.putExtra("SavedNoteID", selected.id)
        startActivityForResult(intent, 1)
    }

    override fun onLongClick(selected: Notes) {
        AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Are you sure you would like to delete this note?")
            .setMessage("This cannot be undone.")
            .setPositiveButton("Yes", object: DialogInterface.OnClickListener {
                override fun onClick(dialogInterface: DialogInterface, i: Int) {
                    CoroutineScope(Dispatchers.Main).launch {
                        deleteNote(selected.id)
                    }

                }
            })
            .setNegativeButton("No", null)
            .show()
    }

}