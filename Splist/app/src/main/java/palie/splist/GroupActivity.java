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
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import palie.splist.model.List;
import palie.splist.rvutils.ListViewHolder;

public class GroupActivity extends AppCompatActivity {

    private String groupKey;
    private int position;
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference mGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setPadding(0, 60, 0, 0);
        setSupportActionBar(toolbar);

        Window w = getWindow();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        LinearLayout content = (LinearLayout) findViewById(R.id.mycontent);
        ImageView image = (ImageView) findViewById(R.id.image_flash);
        final TextView title = (TextView) findViewById(R.id.title);
        final TextView waitingPayment = (TextView) findViewById(R.id.waiting_payment);
        final TextView lists = (TextView) findViewById(R.id.lists);
        final TextView waiting = (TextView) findViewById(R.id.waitingText);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

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
                            int vibrant = d.getValue(Integer.class);
                            waitingPayment.setTextColor(vibrant);
                            break;
                        case "main":
                            int main = d.getValue(Integer.class);
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

        //TODO: change db locations/organize for waiting and unpaid lists.

        RecyclerView activeRV = (RecyclerView) findViewById(R.id.active);
        activeRV.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        FirebaseIndexRecyclerAdapter<List, ListViewHolder> activeAdapter = makeAdapter("active");
        activeRV.setAdapter(activeAdapter);

        RecyclerView waitingRV = (RecyclerView) findViewById(R.id.waiting);
        waitingRV.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        FirebaseIndexRecyclerAdapter<List, ListViewHolder> waitingAdapter = makeAdapter("waiting");
        waitingRV.setAdapter(waitingAdapter);

        RecyclerView unpaidRV = (RecyclerView) findViewById(R.id.unpaid);
        unpaidRV.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        FirebaseIndexRecyclerAdapter<List, ListViewHolder> unpaidAdapter = makeAdapter("unpaid");
        unpaidRV.setAdapter(unpaidAdapter);

        if (unpaidAdapter.getItemCount() == 0) {
            content.removeView(waitingPayment);
        }

        if (waitingAdapter.getItemCount() == 0) {
            content.removeView(waiting);
        }
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
        return builder.create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        switch (item.getItemId()) {
            case R.id.action_add_members:
                return true;
            case R.id.action_change_photo:
                //TODO: regenerate palette
                return true;
            case R.id.rename:
                return true;
            case R.id.delete_group:
                db.getReference("Groups").child(groupKey).removeValue();
                db.getReference("Users").child(uid).child(groupKey).removeValue();
                MainActivity.mGroups.remove(position);
                MainActivity.groupAdapter.notifyItemRemoved(position);
                MainActivity.groupAdapter.notifyItemRangeChanged(position, MainActivity.groupAdapter.getItemCount());
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private FirebaseIndexRecyclerAdapter<List, ListViewHolder> makeAdapter(String type) {

        DatabaseReference ref = db.getReference("Groups").child(groupKey).child(type);
        DatabaseReference source = db.getReference("Lists");
        FirebaseIndexRecyclerAdapter<List, ListViewHolder> adapter = new FirebaseIndexRecyclerAdapter<List, ListViewHolder>
                (List.class, R.layout.list_view, ListViewHolder.class, ref, source) {
            @Override
            protected void populateViewHolder(final ListViewHolder holder, final List list, final int i) {
                FirebaseDatabase.getInstance().getReference("Groups").
                        child(groupKey).child("main").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        holder.getIcon().setFillColor(dataSnapshot.getValue(Integer.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                holder.getName().setText(list.getName());
                switch(list.getType()) {
                    case "Office":
                        holder.getIcon().setImageResource(R.drawable.paperclip);
                        break;
                    case "Clothing":
                        holder.getIcon().setImageResource(R.drawable.ic_hanger);
                        break;
                    case "Food":
                        holder.getIcon().setImageResource(R.drawable.food);
                        break;
                }
                holder.getName().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onListClick(list.getKey(), list.getName());
                    }
                });
            }
        };
        return adapter;
    }

    public void onListClick(String key, String name) {
        Intent i = new Intent(this, ListActivity.class);
        i.putExtra("listkey", key);
        i.putExtra("groupkey", groupKey);
        i.putExtra("name", name);
        startActivity(i);
    }
}
