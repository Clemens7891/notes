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
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import at.fh.swengb.ambrosch.MainActivity.Companion.KEY_USER_TOKEN
import kotlinx.android.synthetic.main.activity_note_list.*

class NoteListActivity : AppCompatActivity() {

    companion object {
        val EXTRA_NOTE_ID = "NOTE_ID_EXTRA"
        val USER_LASTSYNC = "KEY_FOR_USER_LASTSYNC"
        val ADD_OR_EDIT_NOTE_REQUEST = 1
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.listmenu, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            R.id.logout -> {

                val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
                sharedPreferences.edit().clear().apply()

                NoteRepository.deleteAllNote(this)


                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

                true
            }
            R.id.sync -> {
                syncNotes()
                Toast.makeText(this,getString(R.string.syncmsg),Toast.LENGTH_LONG).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }





    val noteAdapter = NoteAdapter(){
        val intent = Intent(this, AddEditNoteActivity ::class.java)
        intent.putExtra(EXTRA_NOTE_ID, it.id)
        startActivityForResult(intent, ADD_OR_EDIT_NOTE_REQUEST)

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)
        syncNotes()


        addNote.setOnClickListener{
            val intent = Intent(this, AddEditNoteActivity::class.java)
            intent.putExtra(EXTRA_NOTE_ID, getString(R.string.NEW_NOTE))
            startActivityForResult(intent, ADD_OR_EDIT_NOTE_REQUEST)


        }

    }



    fun syncNotes(){

        recyclerView.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = noteAdapter

        val myStoredNotes = NoteRepository.getEveryNote(this)
        noteAdapter.updateList(myStoredNotes ?: emptyList())

        val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString(KEY_USER_TOKEN, "")
        Log.e("test", accessToken ?: "kein token")
        val lastSync = sharedPreferences.getLong(getString(R.string.USER_LASTSYNC), 0)


        NoteRepository.getnotes(accessToken ?: "", lastSync,
            success = {
                it.notes.map { NoteRepository.addNote(this, it) }

                sharedPreferences.edit().putLong(USER_LASTSYNC, it.lastSync).apply()

                noteAdapter.updateList(NoteRepository.getEveryNote(this) ?: emptyList())



            },
            error = {
                Toast.makeText(this, getString(R.string.errupload), Toast.LENGTH_LONG).show()
            })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_OR_EDIT_NOTE_REQUEST && resultCode == Activity.RESULT_OK) {
            syncNotes()
        }
    }


}