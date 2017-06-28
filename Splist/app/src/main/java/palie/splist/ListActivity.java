package palie.splist;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import palie.splist.model.Item;
import palie.splist.model.MemberList;
import palie.splist.rvutils.MemberAdapter;
import palie.splist.rvutils.MyItemAdapter;
import palie.splist.listeners.MyItemListener;

public class ListActivity extends AppCompatActivity implements MyItemListener {

    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private String uid;
    private String groupKey, listKey;
    private MyItemAdapter myItemAdapter;
    private ArrayList<Item> myItems;
    private FloatingActionButton fab;
    private LinearLayoutManager layoutManager;

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
        layoutManager = new LinearLayoutManager(this);
        items.setLayoutManager(layoutManager);
        myItemAdapter = new MyItemAdapter(myItems, ListActivity.this);
        items.setAdapter(myItemAdapter);

        DatabaseReference itemRef = db.getReference("Lists").child(listKey).child("items").child(uid).child("items");
        itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Item i = d.getValue(Item.class);
                    myItems.add(i);
                    System.out.println("reading:"+i.getImageKey());
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
        members.setAdapter(new MemberAdapter(memberList, getApplicationContext(), this));

        fab = (FloatingActionButton) findViewById(R.id.fab);
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
    public void onBackPressed() {
        super.onBackPressed();
        layoutManager.findViewByPosition(myItemAdapter.getItemCount() - 2).clearFocus();
    }

    @Override
    protected void onStop() {
        super.onStop();

        final ArrayList<Item> items = new ArrayList<>();
        for (int i = 0; i < myItemAdapter.getItemCount() - 1; i++) {
            Item checklistItem = myItemAdapter.getList().get(i);
            if (checklistItem.getImageKey() == null) {
                items.add(new Item(checklistItem.getItem()));
            } else {
                items.add(new Item(checklistItem.getItem(), checklistItem.getImageKey()));
            }
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

    @Override
    public void uploadImage(final int position, final EditText name) {
        PickSetup setup = new PickSetup()
                .setPickTypes(EPickType.CAMERA, EPickType.GALLERY)
                .setButtonOrientationInt(LinearLayoutCompat.VERTICAL)
                .setSystemDialog(true);
        PickImageDialog.build(setup).setOnPickResult(new IPickResult() {
            @Override
            public void onPickResult(PickResult pickResult) {
                Bitmap result = pickResult.getBitmap();
                String key = db.getReference("Images").push().getKey();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                result.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                UploadTask uploadTask = FirebaseStorage.getInstance().getReference().child(key).putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Upload failed");
                    }
                });
                db.getReference("Lists").child(listKey).child("items").child(uid)
                        .child("items").child(position+"").child("imageKey").setValue(key);
                myItemAdapter.getList().get(position).setImageKey(key);
                name.setPaintFlags(name.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            }
        }).show(this);
    }


}
