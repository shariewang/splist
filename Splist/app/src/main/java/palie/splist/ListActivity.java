package palie.splist;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import palie.splist.listeners.MyItemListener;
import palie.splist.model.Item;
import palie.splist.model.MemberList;
import palie.splist.ocr.AsyncProcessTask;
import palie.splist.ocr.Client;
import palie.splist.ocr.ReceiptSettings;
import palie.splist.rvutils.MemberAdapter;
import palie.splist.rvutils.MyItemAdapter;

public class ListActivity extends AppCompatActivity implements MyItemListener {

    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private String uid;
    private String groupKey, listKey;
    private MyItemAdapter myItemAdapter;
    private ArrayList<Item> myItems;
    private LinearLayoutManager layoutManager;
    private ChildEventListener listCL;
    private AppBarLayout appbar;
    private FloatingActionButton fab;
    private static final int SHOPPING = 1;
    private static final int ONGOING = 2;
    static final int REQUEST_IMAGE_CAPTURE = 0;
    public static final int MEDIA_TYPE_IMAGE = 3;
    public static final int MEDIA_TYPE_VIDEO = 4;
    private String resultUrl = "result.txt";


    // TODO: 6/23/2017 remove all listeners from classes onDetach except notification one.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Window w = getWindow();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getStringExtra("name"));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        appbar = (AppBarLayout) findViewById(R.id.app_bar);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        groupKey = getIntent().getStringExtra("groupkey");
        listKey = getIntent().getStringExtra("listkey");

        setAppBarColor();

        myItems = new ArrayList<>();
        RecyclerView items = (RecyclerView) findViewById(R.id.mylist);
        layoutManager = new LinearLayoutManager(this);
        items.setLayoutManager(layoutManager);
        myItemAdapter = new MyItemAdapter(myItems, ListActivity.this);
        items.setAdapter(myItemAdapter);

        DatabaseReference itemRef = db.getReference("Lists").child(listKey).child("items").child(uid).child("items");
        itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Item i = d.getValue(Item.class);
                    myItems.add(i);
                    System.out.println("reading:"+i.getImageKey());
                }
                myItems.add(new Item());
                myItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final ArrayList<MemberList> memberList = new ArrayList<>();
        listCL = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MemberList m = dataSnapshot.getValue(MemberList.class);
                if (!m.getUid().equals(uid)) {
                    memberList.add(m);
                }
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
        };
        db.getReference("Lists").child(listKey).child("items").addChildEventListener(listCL);

        RecyclerView members = (RecyclerView) findViewById(R.id.members);
        members.animate();
        members.setLayoutManager(new LinearLayoutManager(this));
        members.setAdapter(new MemberAdapter(memberList, getApplicationContext(), this));

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //initial click
                db.getReference("Lists").child(listKey).child("status").setValue(SHOPPING);
                //second click to upload receipt.
                takePictureIntent();
            }
        });

        //disable fab and my checklist after disabled status code
        db.getReference("Lists").child(listKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String buyerUid = "";
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (dataSnapshot.getKey().equals("buyerUid")) {
                        buyerUid = dataSnapshot.getValue(String.class);
                    }
                    if (dataSnapshot.getKey().equals("status")) {
                        int code = dataSnapshot.getValue(Integer.class);
                        if (code == ONGOING && !uid.equals(buyerUid)) {
                            for (int i = 0; i < myItemAdapter.getItemCount() - 1; i++) {
                                View v = layoutManager.findViewByPosition(i);
                                v.findViewById(R.id.itemName).setEnabled(false);
                            }
                            int position = myItems.size() - 1;
                            myItems.remove(position);
                            myItemAdapter.notifyItemRemoved(position);
                            fab.setEnabled(false);
                            //change text color maybe. add locked symbol?
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_list:
                db.getReference("Lists").child(listKey).removeValue();
                db.getReference("Groups").child(groupKey).child("active").child(listKey).removeValue();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (myItemAdapter.getItemCount() >= 2) {
            layoutManager.findViewByPosition(myItemAdapter.getItemCount() - 2).clearFocus();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        final ArrayList<Item> items = new ArrayList<>();
        for (int i = 0; i < myItemAdapter.getItemCount() - 1; i++) {
            Item checklistItem = myItemAdapter.getList().get(i);
            if (checklistItem.getImageKey() == null) {
                items.add(new Item(checklistItem.getItem()));
            } else {
                items.add(new Item(checklistItem.getItem(), checklistItem.getImageKey()));
            }
        }

        //reads name from firebase
        db.getReference("Users").child(uid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                db.getReference("Lists").child(listKey).child("items").child(uid).setValue(new MemberList(uid, name, items));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void uploadImage(final int position, final EditText name) {
        PickSetup setup = new PickSetup()
                .setPickTypes(EPickType.CAMERA, EPickType.GALLERY)
                .setButtonOrientationInt(LinearLayoutCompat.VERTICAL)
                .setSystemDialog(true);
        PickImageDialog.build(setup).setOnPickResult(new IPickResult() {
            @Override
            public void onPickResult(PickResult pickResult) {
                Bitmap result = pickResult.getBitmap();
                String key = db.getReference("Images").push().getKey();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                result.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                UploadTask uploadTask = FirebaseStorage.getInstance().getReference().child(key).putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Upload failed");
                    }
                });
                db.getReference("Lists").child(listKey).child("items").child(uid)
                        .child("items").child(position+"").child("imageKey").setValue(key);
                myItemAdapter.getList().get(position).setImageKey(key);
                name.setPaintFlags(name.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            }
        }).show(this);
    }

    private void setAppBarColor() {
        db.getReference("Groups").child(groupKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().equals("main")) {
                        appbar.setBackgroundColor(ds.getValue(Integer.class));
                    }
                    if (ds.getKey().equals("vibrant")) {
                        fab.setBackgroundTintList(ColorStateList.valueOf(ds.getValue(Integer.class)));
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void takePictureIntent() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri fileUri = getOutputMediaFileUri();
        i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        if (i.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(i, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            String imageFilePath = getOutputMediaFileUri().getPath();
            deleteFile(resultUrl);
            //new AsyncProcessTask(ListActivity.this).execute(imageFilePath, resultUrl);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.getReference("Lists").child(listKey).child("items").removeEventListener(listCL);
    }

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(){
        return Uri.fromFile(getOutputMediaFile());
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }

    public void updateResults(Boolean success) {
        if (!success)
            return;
        try {
            StringBuffer contents = new StringBuffer();

            FileInputStream fis = openFileInput(resultUrl);
            try {
                Reader reader = new InputStreamReader(fis, "UTF-8");
                BufferedReader bufReader = new BufferedReader(reader);
                String text = null;
                while ((text = bufReader.readLine()) != null) {
                    contents.append(text).append(System.getProperty("line.separator"));
                }
            } finally {
                fis.close();
            }

            System.out.println(contents.toString());
        } catch (Exception e) {
        }
    }



}
