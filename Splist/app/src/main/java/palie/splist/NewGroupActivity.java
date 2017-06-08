package palie.splist;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class NewGroupActivity extends AppCompatActivity {

    private EditText groupName;
    private static FirebaseDatabase db = FirebaseDatabase.getInstance();
    private static FirebaseStorage storage = FirebaseStorage.getInstance();
    //TODO: set default
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        setTitle("New group");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
        }

        groupName = (EditText) findViewById(R.id.name);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.save:
                //TODO: if null, assert that there needs to be at least one member or something
                String name = groupName.getText().toString();
                String members = "Persons";
                String key = db.getReference("Groups").push().getKey();

                StorageReference groupPhotos = storage.getReference().child("groupImages").child(key);
                groupPhotos.putFile(imageUri);
                db.getReference("Groups").child(key).setValue(new Group(name, key, members));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
