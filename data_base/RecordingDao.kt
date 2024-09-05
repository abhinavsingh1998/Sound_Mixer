package com.example.soundmixer.data_base

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recordedFile: Recording)

    @Query("SELECT * FROM recording")
    fun getAllRecordedFiles(): LiveData<List<Recording>>

    @Query("DELETE FROM recording WHERE id = :fileId")
    fun deleteById(fileId: Int)

    // Query to delete all records from the table
    @Query("DELETE FROM recording")
    suspend fun deleteAllRecordings()

    @Query("DELETE FROM sqlite_sequence WHERE name = 'recording'")
    suspend fun resetAutoIncrement()

}


