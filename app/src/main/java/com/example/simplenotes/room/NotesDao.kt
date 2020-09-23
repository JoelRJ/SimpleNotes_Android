package com.example.simplenotes.room

import androidx.room.*
import com.example.simplenotes.Notes

// Need to suspend Dao functions to run on UI thread
// https://medium.com/androiddevelopers/room-coroutines-422b786dc4c5
@Dao
interface NotesDao {
    @Query("SELECT * FROM Notes")
    suspend fun getAll(): List<Notes>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(vararg note: Notes)

    @Query("INSERT INTO Notes (note_data) VALUES (:note)")
    suspend fun insertStringNote(note: String)

    @Query("DELETE FROM Notes WHERE id = :idIn")
    suspend fun deleteNote(idIn: Int)

    @Query("DELETE FROM Notes")
    suspend fun nukeNotes()

    @Update
    suspend fun updateNote(note: Notes)
}