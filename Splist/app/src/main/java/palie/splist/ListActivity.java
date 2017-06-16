package palie.splist;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import it.feio.android.checklistview.models.ChecklistManager;
import it.feio.android.checklistview.Settings;
import it.feio.android.checklistview.exceptions.ViewNotSupportedException;
import it.feio.android.checklistview.interfaces.CheckListChangedListener;
import it.feio.android.checklistview.interfaces.Constants;
import palie.splist.model.Item;

public class ListActivity extends AppCompatActivity implements CheckListChangedListener {

    private ArrayList<Item> items;
    private static FirebaseDatabase db = FirebaseDatabase.getInstance();
    static MyListAdapter adapter;
    private ChecklistManager mChecklistManager;
    private View switchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mChecklistManager = ChecklistManager.getInstance(this);
        switchView = (EditText) findViewById(R.id.edittext);
        toggleCheckList();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getStringExtra("name"));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void toggleCheckList() {
        View newView;
        try {
            mChecklistManager.newEntryHint("Add new item");
            mChecklistManager.setCheckListChangedListener(this);

            mChecklistManager.linesSeparator(Constants.LINES_SEPARATOR);
            mChecklistManager.keepChecked(true);
            mChecklistManager.showCheckMarks(false);

            mChecklistManager.dragEnabled(true);
            mChecklistManager.dragVibrationEnabled(false);
            newView = mChecklistManager.convert(switchView);
            mChecklistManager.replaceViews(switchView, newView);
            switchView = newView;

        } catch (ViewNotSupportedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCheckListChanged() {

    }
}
