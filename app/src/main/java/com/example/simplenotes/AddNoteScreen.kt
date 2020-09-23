package com.example.simplenotes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_note_screen.*

class AddNoteScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note_screen)

        finishNoteButton.setOnClickListener {
            val intent = Intent()
            val noteToSend = newNoteInput.text.toString()
            intent.putExtra("newNote", noteToSend)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}