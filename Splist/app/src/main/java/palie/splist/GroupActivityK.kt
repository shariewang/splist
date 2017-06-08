package palie.splist

import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GroupActivityK : AppCompatActivity() {

    private var key: String? = null
    private var toolbarLayout: CollapsingToolbarLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        toolbarLayout = findViewById(R.id.toolbar_layout) as CollapsingToolbarLayout
        val fab = findViewById(R.id.fab) as FloatingActionButton
        val image = findViewById(R.id.image) as ImageView
        //TODO: change
        Glide.with(applicationContext).load("http://s1.dmcdn.net/Cign-/1280x720-coE.jpg").into(image)

        key = intent.getStringExtra("key")
        db.getReference("Groups").child(key!!).child("name").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                toolbarLayout!!.title = dataSnapshot.getValue(String::class.java)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_members -> return true
            R.id.action_change_photo -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private val db = FirebaseDatabase.getInstance()
    }
}