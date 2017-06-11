package palie.splist;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayOutputStream;


public class NewGroupActivity extends AppCompatActivity {

    private EditText groupName;
    private static FirebaseDatabase db = FirebaseDatabase.getInstance();
    private static FirebaseStorage storage = FirebaseStorage.getInstance();
    //TODO: set default
    private Bitmap image;
    private ImageView groupImage;

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
        groupImage = (ImageView) findViewById(R.id.img);
        groupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //upload image
                PickSetup setup = new PickSetup()
                        .setPickTypes(EPickType.CAMERA, EPickType.GALLERY)
                        .setCameraButtonText("Take a photo")
                        .setGalleryButtonText("Choose from gallery")
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

                db.getReference("Groups").child(key).setValue(new Group(name, key, members));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                UploadTask uploadTask = storage.getReference().child(key).putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //TODO: Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        GroupAdapter adapter = MainActivity.groupAdapter;
                        adapter.notifyItemChanged(adapter.getItemCount()-1);
                    }
                });
                createPaletteAsync(image, key);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void createPaletteAsync(Bitmap bitmap, final String key) {
        System.out.println("Started generation");
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                db.getReference("Groups").child(key).child("vibrant").setValue(palette.getVibrantColor(
                        palette.getLightVibrantColor(palette.getDarkVibrantColor(Color.BLUE))
                ));
                db.getReference("Groups").child(key).child("main").setValue(palette.getMutedColor(
                        palette.getLightMutedColor(palette.getDarkMutedColor(palette.getDominantColor(Color.BLUE)))
                ));
            }
        });
    }
}
