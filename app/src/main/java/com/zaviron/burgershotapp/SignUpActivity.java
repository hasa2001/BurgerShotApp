package com.zaviron.burgershotapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {
    public final static String TAG = SignUpActivity.class.getName();
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private EditText signUpEmail, signUpPassword, username, signUpConfirmPassword;
    private Button signUp;
    private TextView signIn;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(intent);
            Toast.makeText(getApplicationContext(), "You Already SignIn", Toast.LENGTH_LONG).show();
            finish();
        } else if (currentUser != null &&
                !currentUser.isEmailVerified()) {
            Toast.makeText(getApplicationContext(), "Please Verify your Email", Toast.LENGTH_LONG).show();
            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        //    username = findViewById(R.id.username);
        signUpEmail = findViewById(R.id.singnupemail);
        signUpPassword = findViewById(R.id.password);
        signUpConfirmPassword = findViewById(R.id.confirmPassword);
        signUp = findViewById(R.id.signUpbtn);
        signIn = findViewById(R.id.signInText);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //    String name = username.getText().toString();
                String email = signUpEmail.getText().toString();
                String password = signUpPassword.getText().toString();
                String confirm_password = signUpConfirmPassword.getText().toString();

//                if (name.isEmpty()) {
//                    Toast.makeText(getApplicationContext(), "Please enter Username", Toast.LENGTH_LONG).show();
//                } else

                if (email.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter Email.", Toast.LENGTH_LONG).show();

                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                    Toast.makeText(getApplicationContext(), "Email is not valid ", Toast.LENGTH_LONG).show();
                } else if (password.isEmpty()) {

                    Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_LONG).show();
                } else if (password.length() <= 8) {
                    Toast.makeText(getApplicationContext(), "Password must be 8 characters or long", Toast.LENGTH_LONG).show();
                } else if (confirm_password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter confirm password", Toast.LENGTH_LONG).show();


                } else if (!confirm_password.equals(password)) {
                    Toast.makeText(getApplicationContext(), "Password and Confirm Password Must Be Same", Toast.LENGTH_LONG).show();
                    System.out.println(confirm_password);
                    System.out.println(password);
                } else {

                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = firebaseAuth.getCurrentUser();
                                        user.sendEmailVerification();
                                        Toast.makeText(getApplicationContext(), " Success Please Verify Your Email", Toast.LENGTH_LONG).show();

                                        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));


                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Login Error", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i(TAG, e.toString());

                                }
                            });
                }
            }
        });

        findViewById(R.id.signInText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                finish();
            }
        });

    }
}