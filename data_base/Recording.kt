package com.example.soundmixer.data_base

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recording",
    indices = [Index(value = ["filePath"], unique = true)] // Ensure fileUrl is unique
)
data class Recording(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fileName: String,
    val filePath:String
)
