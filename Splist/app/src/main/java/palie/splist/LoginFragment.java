package palie.splist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String email, password;
    private EditText emailField, passwordField;
    private TextView forgotPass, register;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        emailField = (EditText) v.findViewById(R.id.email);
        passwordField = (EditText) v.findViewById(R.id.password);
        forgotPass = (TextView) v.findViewById(R.id.forgotPassword);
        register = (TextView) v.findViewById(R.id.register);
        Button signInButton = (Button) v.findViewById(R.id.sign_in_button);

        signInButton.setOnClickListener(this);
        forgotPass.setOnClickListener(this);
        register.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.sign_in_button:
                email = emailField.getText().toString();
                password = passwordField.getText().toString();
                signIn(email, password);
                break;
            case R.id.forgotPassword:
                // TODO: Re-authenticate
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Check your email for instructions to reset your password.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
                break;
            case R.id.register:
                getActivity().getSupportFragmentManager()
                        .beginTransaction().replace(R.id.content, new SignUpFragment()).commit();
                break;
        }
    }

    public void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Login failed",
                            Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(getActivity(), MainActivity.class));
                }
            }
        });
    }
}
