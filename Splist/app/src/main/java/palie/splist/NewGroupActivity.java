package palie.splist;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.util.Rfc822Tokenizer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.ex.chips.BaseRecipientAdapter;
import com.android.ex.chips.RecipientEditTextView;
import com.android.ex.chips.recipientchip.DrawableRecipientChip;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
import palie.splist.model.Group;

public class NewGroupActivity extends AppCompatActivity {

    private EditText groupName;
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    //TODO: set default
    private Bitmap image;
    private static int main, vibrant;
    private ImageView groupImage;
    private RecipientEditTextView groupMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_CONTACTS}, 0);
        }

        setTitle("New group");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
        }

        groupName = (EditText) findViewById(R.id.name);
        groupImage = (ImageView) findViewById(R.id.img);
        groupImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        groupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //upload image
                PickSetup setup = new PickSetup()
                        .setPickTypes(EPickType.CAMERA, EPickType.GALLERY)
                        .setButtonOrientationInt(LinearLayoutCompat.VERTICAL)
                        .setSystemDialog(true);
                PickImageDialog.build(setup).setOnPickResult(new IPickResult() {
                    @Override
                    public void onPickResult(PickResult pickResult) {
                        image = pickResult.getBitmap();
                        groupImage.setImageBitmap(image);
                    }
                }).show(NewGroupActivity.this);
            }
        });
        groupMembers = (RecipientEditTextView) findViewById(R.id.groupMembers);
        groupMembers.setTokenizer(new Rfc822Tokenizer());
        BaseRecipientAdapter baseRecipientAdapter = new BaseRecipientAdapter(this);
        groupMembers.setAdapter(baseRecipientAdapter);
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
                String key = db.getReference("Groups").push().getKey();
                ArrayList<String> emails = new ArrayList<>();
                ArrayList<String> names = new ArrayList<>();
                for (DrawableRecipientChip d : groupMembers.getRecipients()) {
                    String email = d.getValue().toString();
                    emails.add(email);
                    names.add(d.getDisplay().toString());
                    findMemberAndAdd(email, key);
                }

                createPaletteAsyncAndWriteDB(image, name, key, emails, names);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                UploadTask uploadTask = storage.getReference().child(key).putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //TODO: Handle unsuccessful uploads
                    }
                });

                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createPaletteAsyncAndWriteDB(Bitmap bitmap, final String name, final String key,
                                             final ArrayList<String> emails, final ArrayList<String> names) {
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                vibrant = palette.getVibrantColor(
                        palette.getLightVibrantColor(palette.getDarkVibrantColor(palette.getDominantColor(Color.GRAY))));
                main = palette.getMutedColor(palette.getLightMutedColor(
                        palette.getDarkMutedColor(palette.getDominantColor(Color.BLUE))));
                db.getReference("Groups").child(key).setValue(new Group(name, key, emails, names, main, vibrant));
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                db.getReference("Users").child(uid).child("groups").child(key).setValue(key);
            }
        });
    }

    private void findMemberAndAdd(String email, String groupKey) {
        db.getReference("Users").orderByChild("email").startAt(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uid = dataSnapshot.getKey();
                db.getReference("Users").child(uid).child("groups").child(uid).setValue(uid);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
