package palie.splist

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class NewGroupActivityK : AppCompatActivity() {

    private var groupName: EditText? = null
    //TODO: set default
    private val imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_group)

        title = "New group"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_black_24dp)

        groupName = findViewById(R.id.name) as EditText

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.new_group, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.save -> {
                //TODO: if null, assert that there needs to be at least one member or something
                val name = groupName!!.text.toString()
                val members = "Persons"
                val key = db.getReference("Groups").push().key

                val groupPhotos = storage.reference.child("groupImages").child(key)
                groupPhotos.putFile(imageUri!!)
                db.getReference("Groups").child(key).setValue(Group(name, key, members))
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private val db = FirebaseDatabase.getInstance()
        private val storage = FirebaseStorage.getInstance()
    }
}