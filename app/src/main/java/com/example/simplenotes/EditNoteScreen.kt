package com.example.simplenotes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_edit_note_screen.*

class EditNoteScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note_screen)

        val intent = intent
        val passedNote = intent.getStringExtra("SavedNote")
        val passedNoteID = intent.getIntExtra("SavedNoteID",-1)

        if (passedNote != null) {
            editNoteInput.setText(passedNote)
        }

        finishNoteButton.setOnClickListener {
            val intent = Intent()
            val noteToSend = editNoteInput.text.toString()
            intent.putExtra("updatedNote", noteToSend)
            intent.putExtra("updatedNoteID", passedNoteID)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}