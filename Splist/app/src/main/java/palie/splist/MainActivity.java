package palie.splist;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import palie.splist.model.Group;
import palie.splist.rvutils.GroupAdapter;

public class MainActivity extends AppCompatActivity implements GroupClickListener {

    static ArrayList<Group> mGroups;
    static GroupAdapter groupAdapter;
    private ImageView image;
    private Bitmap bitmap;
    private static FirebaseDatabase db = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        StorageReference islandRef = FirebaseStorage.getInstance().getReference().child(user.getUid());
//        final long ONE_MEGABYTE = 1024 * 1024;
//        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                // Data for "images/island.jpg" is returns, use this as needed
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
//            }
//        });

        setTitle("My groups");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_people_black_24dp);
        }

        mGroups = new ArrayList<>();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupAdapter = new GroupAdapter(mGroups, getApplicationContext(), this);
        recyclerView.setAdapter(groupAdapter);

        db.getReference("Users").child(user.getUid()).child("groups").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final String groupKey = dataSnapshot.getValue(String.class);
                db.getReference("Groups").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (ds.getKey().equals(groupKey)) {
                                Group group = ds.getValue(Group.class);
                                mGroups.add(group);
                                groupAdapter.notifyItemInserted(mGroups.size() - 1);
                                //groupAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_group:
                startActivity(new Intent(getApplicationContext(), NewGroupActivity.class));
                return true;
            case R.id.signOut:
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onGroupClick(int position, String key, ImageView sharedImageView) {
        Intent i = new Intent(this, GroupActivity.class);
        i.putExtra("key", key);
        i.putExtra("position", position);
        ActivityOptions options = ActivityOptions
                .makeSceneTransitionAnimation(this, Pair.create((View)sharedImageView, key));
        startActivity(i, options.toBundle());
    }
}
