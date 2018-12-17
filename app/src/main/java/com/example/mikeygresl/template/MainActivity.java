package com.example.mikeygresl.template;

import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

//class for login

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "Main Activity";

    //layout elements
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signinButton;
    private TextView signupLink;

    //signin input elements
    private String email;
    private String password;

    //all users in FB
    private static List<User> users;

    //Firebase elements
    private FirebaseAuth authentication;                //Firebase authentication

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initLayout();
        authentication = FirebaseAuth.getInstance();

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {        //check for empty fields

                    Toast.makeText(getApplicationContext(), "Empty Fields!", Toast.LENGTH_SHORT);
                }
                else {
                    signIn(email, password);
                }


            }
        });

        //link to singup if user doesn't have an account yet
        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), Signup.class);
                finish();
                startActivity(intent);
            }
        });

    }

    //implementing native Firebase signIn function
    //if successful -> go to contacts activity, which contains all user contacts
    private void signIn(final String email, final String password) {

        authentication.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (authentication.getCurrentUser() != null) {

                    finish();
                    startActivity(new Intent(getApplicationContext(), Contacts.class));
                }
                else {

                    if (task.isSuccessful()) {

                        Log.d(TAG, "signInWithEmail:success");
                        Intent intent = new Intent(getApplicationContext(), Contacts.class);
                        finish();
                        startActivity(intent);
                    }
                    else {

                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(getApplicationContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });


    }

    //initialize xml layout elements
    private void initLayout() {

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signinButton = findViewById(R.id.signupButton);
        signupLink = findViewById(R.id.signupTextView);
    }
}
