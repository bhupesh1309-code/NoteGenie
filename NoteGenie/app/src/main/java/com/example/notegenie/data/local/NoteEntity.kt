package com.example.notegenie.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val topic: String,
    val content: String,
    val dateGenerated: String,
    val timestamp: Long = System.currentTimeMillis()
)