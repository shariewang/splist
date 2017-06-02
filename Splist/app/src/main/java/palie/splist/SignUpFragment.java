package palie.splist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpFragment extends Fragment implements View.OnClickListener {

    private String name, email, password;
    private EditText emailField, passwordField, nameField;
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sign_up, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        emailField = (EditText) v.findViewById(R.id.email);
        passwordField = (EditText) v.findViewById(R.id.password);
        nameField = (EditText) v.findViewById(R.id.name);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                getActivity().getSupportFragmentManager()
                        .beginTransaction().replace(R.id.content, new LoginFragment()).commit();
                break;
            case R.id.register_button:
                name = nameField.getText().toString();
                email = emailField.getText().toString();
                password = passwordField.getText().toString();
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
                            userRef.child(mAuth.getCurrentUser().getUid()).child("name").setValue(name);
                            userRef.child(mAuth.getCurrentUser().getUid()).child("email").setValue(email);
                            startActivity(new Intent(getActivity(), MainActivity.class));
                        }
                    }
                });
    }
}
