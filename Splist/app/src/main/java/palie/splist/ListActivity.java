package palie.splist;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import it.feio.android.checklistview.models.CheckListViewItem;
import it.feio.android.checklistview.models.CheckListView;
import it.feio.android.checklistview.models.ChecklistManager;
import it.feio.android.checklistview.exceptions.ViewNotSupportedException;
import it.feio.android.checklistview.interfaces.CheckListChangedListener;
import it.feio.android.checklistview.interfaces.Constants;
import palie.splist.rvutils.MyListAdapter;

public class ListActivity extends AppCompatActivity {

    private ArrayList<CheckListViewItem> items;
    private static FirebaseDatabase db = FirebaseDatabase.getInstance();
    static MyListAdapter adapter;
    private ChecklistManager mChecklistManager;
    private CheckListView checklist;
    private boolean editMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

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
            case R.id.edit:
                editMode = !editMode;
                if (editMode) {
                    item.setIcon(R.drawable.ic_check_white_24dp);
                    checklist.addHintItem();
                } else {
                    //save just got clicked
                    item.setIcon(R.drawable.ic_edit_white_24dp);
                    for (int i = 0; i < mChecklistManager.getCount(); i++) { //exclude hint
                        items.add(checklist.getChildAt(i));
                    }
                }
                mChecklistManager.toggleDragHandler(checklist, editMode);
                return true;
            case R.id.delete_list:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
