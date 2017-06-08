package palie.splist

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import java.util.ArrayList

class MainActivityK : AppCompatActivity() {

    private var mGroups: MutableList<Group>? = null
    private var groupAdapter: GroupAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "My groups"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_person_outline_black_24dp)

        mGroups = ArrayList<Group>()

        val recyclerView = findViewById(R.id.recyclerView) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        groupAdapter = GroupAdapter(mGroups, applicationContext)
        recyclerView.adapter = groupAdapter

        db.getReference("Groups").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String) {
                val group = dataSnapshot.getValue(Group::class.java)
                mGroups!!.add(group)
                groupAdapter!!.notifyItemInserted(mGroups!!.size - 1)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String) {}

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String) {}

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.new_group -> {
                startActivity(Intent(applicationContext, NewGroupActivity::class.java))
                return true
            }
            R.id.signOut -> {
                FirebaseAuth.getInstance().signOut()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private val db = FirebaseDatabase.getInstance()
    }

}
