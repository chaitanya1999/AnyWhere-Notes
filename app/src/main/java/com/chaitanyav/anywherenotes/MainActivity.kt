package com.chaitanyav.anywherenotes

import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout

class MainActivity : AppCompatActivity() {

    private var currentNote: Note? = null
    fun setCurrentNote(note:Note){
        currentNote = note
    }
    fun getCurrentNote(): Note? {
        return currentNote
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Globals.dbHelper = DatabaseHelper(applicationContext)
        Globals.noteListFragment = NoteListFragment()

        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer,Globals.noteListFragment).commit()
    }

    override fun onBackPressed() {
        if(supportFragmentManager.findFragmentById(R.id.fragmentContainer) is NoteFragment){
            if (Globals.noteFragment.textChanged) {
                val builder = android.support.v7.app.AlertDialog.Builder(this!!)
                builder.setTitle("Confirm").setMessage("There are some unsaved changes. Are you sure you want to quit?")
                        .setPositiveButton("Yes") { dialogInterface, i -> super.onBackPressed()}.setNegativeButton("No") { dialogInterface, i -> dialogInterface.cancel() }.show()
            } else {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }

    }
}
