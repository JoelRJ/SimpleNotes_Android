package com.example.simplenotes

import RecyclerAdapter
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
//        Toast.makeText(applicationContext, returnString, Toast.LENGTH_SHORT)
        if (returnString != null) {
            addNote(returnString)
        }
        else {
            Log.d("It was a NULL!", ":(")
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

    private fun deleteAll() {

        var alert = AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Are you sure you would like to delete ALL notes?")
            .setMessage("This cannot be undone.")
            .setPositiveButton("Yes", object: DialogInterface.OnClickListener {
                override fun onClick(dialogInterface: DialogInterface, i: Int) {
                    Toast.makeText(applicationContext, "Test", Toast.LENGTH_LONG)
                    CoroutineScope(Dispatchers.IO).launch {
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

    fun updateNotesView(listIn: List<Notes> = arrayListOf()) {
        notesView.adapter = RecyclerAdapter(listIn, this, this)
    }

    override fun onCellClickListener(selected: Notes) {
        Log.d("Clicked on", selected.noteData)
    }

}