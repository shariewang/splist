package palie.splist;

import android.content.DialogInterface;
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

public class GroupActivity extends AppCompatActivity {

    private String groupKey;
    private int position;
    private static FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference mGroup;
    private GroupAdapter adapter = MainActivity.groupAdapter;
    private ArrayList<Group> groups = MainActivity.mGroups;
    private ArrayList<List> activeLists, unpaidLists, waitingLists;
    private ActiveAdapter activeAdapter;
    private UnpaidAdapter unpaidAdapter;
    private WaitingAdapter waitingAdapter;

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
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        LinearLayoutManager llm = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        activeLists = new ArrayList<>();
        unpaidLists = new ArrayList<>();
        waitingLists = new ArrayList<>();
        RecyclerView unpaidRV = (RecyclerView) findViewById(R.id.unpaid);
        RecyclerView activeRV = (RecyclerView) findViewById(R.id.active);
        RecyclerView waitingRV = (RecyclerView) findViewById(R.id.waiting);
        unpaidRV.setLayoutManager(llm);
        activeRV.setLayoutManager(llm);
        waitingRV.setLayoutManager(llm);
        unpaidAdapter = new UnpaidAdapter(unpaidLists, getApplicationContext());
        activeAdapter = new ActiveAdapter(activeLists, getApplicationContext());
        waitingAdapter = new WaitingAdapter(waitingLists, getApplicationContext());
        unpaidRV.setAdapter(unpaidAdapter);
        activeRV.setAdapter(activeAdapter);
        waitingRV.setAdapter(waitingAdapter);

        groupKey = getIntent().getStringExtra("groupKey");
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
                            waitingPayment.setTextColor(d.getValue(Integer.class));
                            break;
                        case "main":
                            lists.setTextColor(d.getValue(Integer.class));
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
        builder.setTitle("New list")
                .setView(view)
                .setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String key = db.getReference("Lists").push().getKey();
                        db.getReference("Lists").child(key).setValue(new List(key, name.getText().toString()));
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter
                .createFromResource(this, R.array.list_categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) view.findViewById(R.id.category)).setAdapter(categoryAdapter);
        ArrayAdapter<CharSequence> colorAdapter = ArrayAdapter
                .createFromResource(this, R.array.list_colors, android.R.layout.simple_spinner_item);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) view.findViewById(R.id.color)).setAdapter(colorAdapter);
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
                groups.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeRemoved(position, adapter.getItemCount());
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
