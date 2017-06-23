package palie.splist;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.lang.reflect.Member;
import java.util.ArrayList;

import it.feio.android.checklistview.models.CheckListViewItem;
import it.feio.android.checklistview.models.CheckListView;
import it.feio.android.checklistview.models.ChecklistManager;
import it.feio.android.checklistview.exceptions.ViewNotSupportedException;
import it.feio.android.checklistview.interfaces.Constants;
import palie.splist.model.Item;
import palie.splist.model.MemberList;
import palie.splist.rvutils.MemberHolder;

public class ListActivity extends AppCompatActivity {

    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private ArrayList<Item> items;
    private ChecklistManager mChecklistManager;
    private CheckListView checklist;
    private String uid;
    private boolean editMode;
    private int position;
    private String groupKey, listKey;
    private FirebaseIndexRecyclerAdapter<MemberList, MemberHolder> mAdapter;
    private RecyclerView members;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getStringExtra("name"));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        position = getIntent().getIntExtra("position", 0);
        groupKey = getIntent().getStringExtra("groupkey");
        listKey = getIntent().getStringExtra("listkey");
        setUpChecklist();
        items = new ArrayList<>();

        members = (RecyclerView) findViewById(R.id.members);
        members.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference source = db.getReference("Items");
        DatabaseReference ref = db.getReference("Lists").child(listKey).child("items");
        mAdapter = new FirebaseIndexRecyclerAdapter<MemberList, MemberHolder>(
                MemberList.class, R.layout.member_list_card, MemberHolder.class, ref, source) {
            @Override
            public void populateViewHolder(MemberHolder holder, MemberList memberlist, int position) {
                Glide.with(getApplicationContext()).using(new FirebaseImageLoader())
                        .load(storage.getReference().child(uid)).into(holder.getImageView());
                holder.getTextView().setText(memberlist.getName());
                ListView list = holder.getListView();
                DatabaseReference dbRef = db.getReference("Items").child(uid).child("items");
                FirebaseListAdapter<Item> adapter = new FirebaseListAdapter<Item>(ListActivity.this, Item.class,
                        R.layout.checkbox_guest, dbRef) {
                    @Override
                    protected void populateView(View view, Item item, int i) {
                        CheckBox cb = (CheckBox) view.findViewById(R.id.checkBox);
                        TextView name = (TextView) view.findViewById(R.id.itemName);
                        System.out.println(item.getItem());
                        cb.setChecked(item.getChecked());
                        name.setText(item.getItem());
                    }
                };
                list.setAdapter(adapter);
            }
        };
        members.setAdapter(mAdapter);

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
            case R.id.edit:
                editMode = !editMode;
                if (editMode) {
                    item.setIcon(R.drawable.ic_check_white_24dp);
                    checklist.addHintItem();
                } else {
                    //save just got clicked
                    items.clear();
                    item.setIcon(R.drawable.ic_edit_white_24dp);
                    for (int i = 0; i < mChecklistManager.getCount(); i++) {
                        CheckListViewItem checklistItem = checklist.getChildAt(i);
                        items.add(new Item(checklistItem.getText()));
                    }

                    db.getReference("Lists").child(listKey).child("items").child(uid).setValue(uid);
                    //reads name from firebase
                    db.getReference("Users").child(uid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String name = dataSnapshot.getValue(String.class);
                            db.getReference("Items").child(uid).setValue(new MemberList(name, items));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                mChecklistManager.toggleDragHandler(checklist, editMode);
                return true;
            case R.id.delete_list:
                db.getReference("Lists").child(listKey).removeValue();
                db.getReference("Groups").child(groupKey).child("active").child(listKey).removeValue();
                GroupActivity.activeLists.remove(position);
                GroupActivity.activeAdapter.notifyItemRemoved(position);
                GroupActivity.activeAdapter.notifyItemRangeRemoved(position, GroupActivity.activeAdapter.getItemCount());
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setUpChecklist() {
        mChecklistManager = new ChecklistManager(this);
        mChecklistManager.linesSeparator(Constants.LINES_SEPARATOR);
        mChecklistManager.keepChecked(false);
        mChecklistManager.showCheckMarks(false);
        mChecklistManager.dragEnabled(true);
        mChecklistManager.dragVibrationEnabled(false);
        mChecklistManager.newEntryHint("Add new item");
        View switchView = findViewById(R.id.edittext);
        View newView = null;
        try {
            newView = mChecklistManager.convert(switchView);

        } catch (ViewNotSupportedException e) {
            e.printStackTrace();
        }
        mChecklistManager.replaceViews(switchView, newView);
        checklist = (CheckListView) newView;
        editMode = false;
        mChecklistManager.toggleDragHandler(checklist, true);
        mChecklistManager.toggleDragHandler(checklist, false);
    }
}
