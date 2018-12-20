package com.pachecoluc.geolocaltest;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
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

public class ConnexionActivity extends AppCompatActivity implements View.OnClickListener{

    //CONNEXION
    private FirebaseAuth mAuth;

    //INFORMATIONS DE CONNEXION
    String emailAdress;
    String password;

    //LAYOUT ELEMENTS
    Button connectButton;
    EditText emailInput;
    EditText passwordInput;
    TextView titleView;

    String TAG = "tag";

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        mAuth = FirebaseAuth.getInstance();

        titleView = findViewById(R.id.title);
        connectButton = findViewById(R.id.button);
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);

        connectButton.setOnClickListener(this);

        intent = new Intent(this, GpsActivity.class);
    }

    protected void updateUI(FirebaseUser firebaseUser){
        if(firebaseUser != null){
            titleView.setText("You are logged.");
            Toast.makeText(getBaseContext(),"Authentification SUCESS",Toast.LENGTH_LONG).show();
            startActivity(intent);
        }
        if(firebaseUser == null){
            Toast.makeText(getBaseContext(),"Authentification failed",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        signIn(emailInput.getText().toString(),passwordInput.getText().toString());
    }

    public void createAccount(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(ConnexionActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);

                        }
                        // ...
                    }
                });

    }

    public void signIn(String email, String password){
        Log.d(TAG,"In signIn");
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(ConnexionActivity.this, "Authentication success.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(ConnexionActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            createAccount(emailInput.getText().toString(),passwordInput.getText().toString());
                            //updateUI(null);
                        }
                        // ...
                    }
                }
        );
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            // Check if user is signed in (non-null) and update UI accordingly.
            FirebaseUser currentUser = mAuth.getCurrentUser();
            emailAdress = currentUser.getEmail();
            updateUI(currentUser);
        }catch(Exception e){
            Log.d(TAG, "error : "+e.getMessage());
        }
    }

}
