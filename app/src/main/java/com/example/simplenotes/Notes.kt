package com.example.simplenotes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Notes(
    @PrimaryKey(autoGenerate = true) var id: Int?,
    @ColumnInfo(name = "note_data") val noteData: String?
)