package at.fh.swengb.ambrosch

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy

@Dao

interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: Note)

    @Query("SELECT * FROM NOTE WHERE id = :id")
    fun getSingleNote(id: String): Note?

    @Query("DELETE FROM Note")
    fun deleteAllNote()

    @Query("SELECT * FROM Note")
    fun getEveryNote(): List<Note>?


}