package at.fh.swengb.ambrosch

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import at.fh.swengb.ambrosch.MainActivity.Companion.KEY_USER_TOKEN
import kotlinx.android.synthetic.main.activity_note.*
import java.util.*

class AddEditNoteActivity : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.savenote, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.saveNote -> {
                var containsText: Boolean = false
                val inputTitle: String? = NoteTitle.text.toString()
                val inputContent: String? = NoteContent.text.toString()
                if (inputTitle != "" || inputContent != "") {
                    containsText = true
                }

                if (containsText) {
                    val noteIDforSaveOrEdit = intent.getStringExtra(NoteListActivity.EXTRA_NOTE_ID)
                    Log.e("test", noteIDforSaveOrEdit ?: "kein intent")
                    if (noteIDforSaveOrEdit == getString(R.string.NEW_NOTE) || noteIDforSaveOrEdit == null) {
                        val uuidString = UUID.randomUUID().toString()
                        val newNoteToSave =
                            Note(uuidString, inputTitle ?: "", inputContent ?: "", true)
                        saveandupload(newNoteToSave)

                    } else {
                        val editNote = Note(
                            noteIDforSaveOrEdit ?: "",
                            inputTitle ?: "",
                            inputContent ?: "",
                            true
                        )
                        saveandupload(editNote)
                    }

                } else {
                    Toast.makeText(this, getString(R.string.errinput), Toast.LENGTH_LONG).show()
                }

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    fun saveandupload(inputNote: Note) {
        NoteRepository.addNote(this, inputNote)
        val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString(KEY_USER_TOKEN, "")
        Log.e("token", accessToken ?: "kein token")
        NoteRepository.addOrUpdateNote(accessToken ?:"", inputNote,
            success = {
                NoteRepository.addNote(this, it)
                val resultIntent = Intent(this, NoteListActivity::class.java)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            },
            error = {
                Toast.makeText(this, getString(R.string.errupload), Toast.LENGTH_LONG).show()
            })

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        val noteID = intent.getStringExtra(NoteListActivity.EXTRA_NOTE_ID)
        Log.e("test", noteID ?: "kein intent")
        if (noteID != null) {
            val StoredNote = NoteRepository.getSingleNote(this, noteID)
            NoteTitle.setText(StoredNote?.title ?: "")
            NoteContent.setText(StoredNote?.text ?: "")
        }


    }
}