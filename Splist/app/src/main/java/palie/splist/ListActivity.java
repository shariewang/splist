package palie.splist;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import palie.splist.model.Item;
import palie.splist.model.MemberList;
import palie.splist.rvutils.ItemAdapter;
import palie.splist.rvutils.MemberAdapter;
import palie.splist.rvutils.MyItemAdapter;

public class ListActivity extends AppCompatActivity {

    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private String uid;
    private String groupKey, listKey;
    private ItemAdapter adapter;
    private MyItemAdapter myItemAdapter;
    private ArrayList<Item> myItems;

    // TODO: 6/23/2017 remove all listeners from classes onDetach

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getStringExtra("name"));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        groupKey = getIntent().getStringExtra("groupkey");
        listKey = getIntent().getStringExtra("listkey");

        myItems = new ArrayList<>();
        RecyclerView items = (RecyclerView) findViewById(R.id.mylist);
        items.setLayoutManager(new LinearLayoutManager(this));
        myItemAdapter = new MyItemAdapter(myItems, getApplicationContext());
        items.setAdapter(myItemAdapter);

        DatabaseReference itemRef = db.getReference("Lists").child(listKey).child("items").child(uid).child("items");
        itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Item i = d.getValue(Item.class);
                    myItems.add(i);
                }
                myItems.add(new Item());
                myItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final ArrayList<MemberList> memberList = new ArrayList<>();

        db.getReference("Lists").child(listKey).child("items").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MemberList m = dataSnapshot.getValue(MemberList.class);
                if (!m.getUid().equals(uid)) {
                    memberList.add(m);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        RecyclerView members = (RecyclerView) findViewById(R.id.members);
        members.animate();
        members.setLayoutManager(new LinearLayoutManager(this));
        members.setAdapter(new MemberAdapter(memberList, getApplicationContext()));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_list:
                db.getReference("Lists").child(listKey).removeValue();
                db.getReference("Groups").child(groupKey).child("active").child(listKey).removeValue();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        final ArrayList<Item> items = new ArrayList<>();
        for (int i = 0; i < myItemAdapter.getItemCount() - 1; i++) {
            Item checklistItem = myItemAdapter.getList().get(i);
            items.add(new Item(checklistItem.getItem()));
        }

        //reads name from firebase
        db.getReference("Users").child(uid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                db.getReference("Lists").child(listKey).child("items").child(uid).setValue(new MemberList(uid, name, items));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
