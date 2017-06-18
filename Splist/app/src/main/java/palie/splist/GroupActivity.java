package palie.splist;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import palie.splist.model.List;
import palie.splist.rvutils.ActiveAdapter;
import palie.splist.rvutils.UnpaidAdapter;
import palie.splist.rvutils.WaitingAdapter;

public class GroupActivity extends AppCompatActivity implements ListClickListener {

    private String groupKey;
    private int position;
    private static FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference mGroup;
    private ArrayList<List> activeLists, unpaidLists, waitingLists;
    private ActiveAdapter activeAdapter;
    private UnpaidAdapter unpaidAdapter;
    private WaitingAdapter waitingAdapter;
    private int vibrant, main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        ImageView image = (ImageView) findViewById(R.id.image);
        final TextView title = (TextView) findViewById(R.id.title);
        final TextView waitingPayment = (TextView) findViewById(R.id.waiting_payment);
        final TextView lists = (TextView) findViewById(R.id.lists);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        LinearLayoutManager llm1 = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        LinearLayoutManager llm2 = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        LinearLayoutManager llm3 = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        activeLists = new ArrayList<>();
        unpaidLists = new ArrayList<>();
        waitingLists = new ArrayList<>();

        groupKey = getIntent().getStringExtra("key");
        position = getIntent().getIntExtra("position", 0);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog().show();
            }
        });
        image.setTransitionName(groupKey);
        Glide.with(this).using(new FirebaseImageLoader())
                .load(FirebaseStorage.getInstance().getReference().child(groupKey)).into(image);

        //TODO: change model class and firebase
        mGroup = db.getReference("Groups").child(groupKey);
        mGroup.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    switch(d.getKey()) {
                        case "name":
                            title.setText(d.getValue(String.class));
                            break;
                        case "vibrant":
                            vibrant = d.getValue(Integer.class);
                            waitingPayment.setTextColor(vibrant);
                            break;
                        case "main":
                            main = d.getValue(Integer.class);
                            lists.setTextColor(main);
                            fab.setBackgroundTintList(ColorStateList.valueOf(main));
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        handleListListeners(1);
        handleListListeners(2);
        handleListListeners(3);

        RecyclerView unpaidRV = (RecyclerView) findViewById(R.id.unpaid);
        RecyclerView activeRV = (RecyclerView) findViewById(R.id.active);
        RecyclerView waitingRV = (RecyclerView) findViewById(R.id.waiting);
        unpaidRV.setLayoutManager(llm1);
        activeRV.setLayoutManager(llm2);
        waitingRV.setLayoutManager(llm3);
        unpaidAdapter = new UnpaidAdapter(unpaidLists, getApplicationContext(), this);
        activeAdapter = new ActiveAdapter(activeLists, getApplicationContext(), this);
        waitingAdapter = new WaitingAdapter(waitingLists, getApplicationContext(), this);
        unpaidRV.setAdapter(unpaidAdapter);
        activeRV.setAdapter(activeAdapter);
        waitingRV.setAdapter(waitingAdapter);
    }

    private void getAndAdd(final String key, final int type) {
        db.getReference("Lists").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().equals(key)) {
                        List list = ds.getValue(List.class);
                        switch(type) {
                            case 1: //unpaid
                                unpaidLists.add(list);
                                unpaidAdapter.notifyItemInserted(unpaidLists.size() - 1);
                                break;
                            case 2: //active
                                activeLists.add(list);
                                activeAdapter.notifyItemInserted(activeLists.size() - 1);
                                break;
                            case 3: //waiting
                                waitingLists.add(list);
                                waitingAdapter.notifyItemInserted(waitingLists.size() - 1);
                                break;
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void handleListListeners(final int type) {
        String typeString = "";
        switch(type) {
            case 1:
                typeString = "unpaid";
                break;
            case 2:
                typeString = "active";
                break;
            case 3:
                typeString = "waiting";
                break;
        }
        mGroup.child(typeString).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getValue(String.class);
                getAndAdd(key, type);
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
    }

    public AlertDialog createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_new_list, null);
        final TextView name = (TextView) view.findViewById(R.id.name);
        final Spinner spinner = (Spinner) view.findViewById(R.id.category);
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter
                .createFromResource(this, R.array.list_categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(categoryAdapter);
        builder.setTitle("New list")
                .setView(view)
                .setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String key = db.getReference("Lists").push().getKey();
                        db.getReference("Lists").child(key).setValue(new List(key, name.getText().toString(), spinner.getSelectedItem().toString()));
                        db.getReference("Groups").child(groupKey).child("active").child(key).setValue(key);
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

//        ArrayAdapter<CharSequence> colorAdapter = ArrayAdapter
//                .createFromResource(this, R.array.list_colors, android.R.layout.simple_spinner_item);
//        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        ((Spinner) view.findViewById(R.id.color)).setAdapter(colorAdapter);
        return builder.create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_members:
                return true;
            case R.id.action_change_photo:
                //TODO: regenerate palette
                return true;
            case R.id.delete_group:
                db.getReference("Groups").child(groupKey).removeValue();
                MainActivity.mGroups.remove(position);
                MainActivity.groupAdapter.notifyItemRemoved(position);
                MainActivity.groupAdapter.notifyItemRangeRemoved(position, MainActivity.groupAdapter.getItemCount());
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListClick(int position, String key, String name) {
        Intent i = new Intent(this, ListActivity.class);
        i.putExtra("key", key);
        i.putExtra("name", name);
        i.putExtra("position", position);
        startActivity(i);
    }
}
