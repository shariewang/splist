package palie.splist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpFragment extends Fragment implements View.OnClickListener {

    private String name;
    private EditText emailField, passwordField, nameField;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private CircleImageView image;
    private Bitmap result;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sign_up, container, false);

        emailField = (EditText) v.findViewById(R.id.email);
        passwordField = (EditText) v.findViewById(R.id.password);
        nameField = (EditText) v.findViewById(R.id.name);
        image = (CircleImageView) v.findViewById(R.id.profilepic);
        TextView login = (TextView) v.findViewById(R.id.login);
        Button register = (Button) v.findViewById(R.id.register_button);
        register.setOnClickListener(this);
        image.setOnClickListener(this);
        login.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profilepic:
                PickSetup setup = new PickSetup()
                        .setPickTypes(EPickType.CAMERA, EPickType.GALLERY)
                        .setButtonOrientation(LinearLayoutCompat.VERTICAL)
                        .setSystemDialog(true);
                PickImageDialog.build(setup).setOnPickResult(new IPickResult() {
                    @Override
                    public void onPickResult(PickResult pickResult) {
                        result = pickResult.getBitmap();
                        image.setImageBitmap(result);
                    }
                }).show(getFragmentManager());
                break;
            case R.id.login:
                getActivity().getSupportFragmentManager().
                        beginTransaction().replace(R.id.content, new LoginFragment()).commit();
                break;
            case R.id.register_button:
                name = nameField.getText().toString();
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();
                register(email, password);
                break;
        }
    }

    public void register(final String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Registration failed", Toast.LENGTH_SHORT).show();
                        } else {
                            DatabaseReference userRef = db.getReference("Users");
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            result.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();
                            String uid = mAuth.getCurrentUser().getUid();
                            UploadTask uploadTask = FirebaseStorage.getInstance().getReference().child(uid).putBytes(data);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    System.out.println("Upload failed");
                                }
                            });
                            userRef.child(uid).child("name").setValue(name);
                            userRef.child(uid).child("email").setValue(email);
                            startActivity(new Intent(getActivity(), MainActivity.class));
                        }
                    }
                });
    }
}
