package com.zaviron.burgershotapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.LauncherActivity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.GetSignInIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInActivity extends AppCompatActivity {

    public static final String TAG = SignInActivity.class.getName();
    private EditText email, password;
    private Button SignIn;

    private FirebaseAuth firebaseAuth;
    private SignInClient signInClient;

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));

        }
        //else if (!currentUser.isEmailVerified()) {
//            Toast.makeText(getApplicationContext(), "Please Verify Your Email", Toast.LENGTH_LONG).show();
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        email = findViewById(R.id.signInEmail);
        password = findViewById(R.id.signInPassword);
        firebaseAuth = FirebaseAuth.getInstance();
        signInClient = Identity.getSignInClient(getApplicationContext());

        findViewById(R.id.signInBtn).setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                String userEmail = email.getText().toString();
                String userPassword = password.getText().toString();
                if (userEmail.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter Email", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "Ok");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                    Toast.makeText(getApplicationContext(), "Please enter valid Email", Toast.LENGTH_LONG).show();
                } else if (userPassword.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter Password", Toast.LENGTH_LONG).show();
                } else {

                    firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.i(TAG, "success : " + task.getResult().toString());
                                        startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                        finish();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i(TAG, e.toString());
                                    Toast.makeText(getApplicationContext(), "Details Incorrect Try Again", Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });


        findViewById(R.id.SignUpText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                finish();
            }
        });
        findViewById(R.id.forgotPassword).setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Log.i(TAG, "OK Forgot Password is Working");
                AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
                View dialog = getLayoutInflater().inflate(R.layout.dialog_forgot, null);
                builder.setView(dialog);
                AlertDialog alertDialog = builder.create();

                EditText emailEditText = dialog.findViewById(R.id.forgotEmailField);
                dialog.findViewById(R.id.forgotResetBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email = emailEditText.getText().toString();
                        if (email.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Please Add Email", Toast.LENGTH_LONG).show();
                        } else if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            Toast.makeText(getApplicationContext(), "Please Enter valid Email", Toast.LENGTH_LONG).show();
                        } else {
                            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Please check your email", Toast.LENGTH_LONG).show();
                                        alertDialog.dismiss();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "email sending failed ", Toast.LENGTH_LONG).show();
                                        alertDialog.dismiss();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "User not Sign Up  Please SignUp First ", Toast.LENGTH_LONG).show();
                                    alertDialog.dismiss();
                                }
                            });
                        }
                    }
                });

                dialog.findViewById(R.id.forgotCancelBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });
        findViewById(R.id.signInWithGoogle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GetSignInIntentRequest signInIntentRequest = GetSignInIntentRequest.builder()
                        .setServerClientId(getString(R.string.web_client_id)).build();
                Task<PendingIntent> signInPendingIntent = signInClient.getSignInIntent(signInIntentRequest);
                signInPendingIntent.addOnSuccessListener(new OnSuccessListener<PendingIntent>() {
                    @Override
                    public void onSuccess(PendingIntent pendingIntent) {
                        IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(pendingIntent).build();
                        signInLauncher.launch(intentSenderRequest);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, e.toString());
                    }
                });
            }
        });
    }

    private final ActivityResultLauncher<IntentSenderRequest> signInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    handleGoogleSignIn(o.getData());

                }
            });

    private void handleGoogleSignIn(Intent intent) {
        try {
            SignInCredential signInCredentialFromIntent = signInClient.getSignInCredentialFromIntent(intent);
            String idToken = signInCredentialFromIntent.getGoogleIdToken();
            firebaseAuthWithGoogleAccount(idToken);
        } catch (ApiException e) {
            Log.i(TAG, e.getMessage());
        }

    }

    private void firebaseAuthWithGoogleAccount(String idToken) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                    if (currentUser != null) {
                        startActivity(new Intent(SignInActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Error ", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "SignInFailed");
            }
        });
    }
}