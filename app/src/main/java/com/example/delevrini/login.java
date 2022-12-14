package com.example.delevrini;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.delevrini.Chef.ChefActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity {

    private Button login;
    private TextView forget, textsignup;
    private EditText email;
    private EditText password;
    private FirebaseAuth auth;
    private DatabaseReference UserDatabase;
    private Boolean  chefReference, clientReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        textsignup = findViewById(R.id.text_signup);
        forget = findViewById(R.id.forget);

        auth = FirebaseAuth.getInstance();
        UserDatabase = FirebaseDatabase.getInstance().getReference("Users");

        textsignup.setOnClickListener(v -> {
            Intent i = new Intent(login.this, signin.class);
            startActivity(i);
        });

        forget.setOnClickListener(view -> {
            final EditText input = new EditText(this);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Set Your Email Recover Password")
                    // Set up the input
                    .setView(input)
                    // Set up the buttons
                    .setPositiveButton("OK", (dialog, which) -> {
                        String m_Text = input.getText().toString();
                        if (m_Text.length() < 1) {
                            input.setError("add your email address");

                        } else {
                            auth.sendPasswordResetEmail(m_Text).addOnCompleteListener(task -> {
                                Toast.makeText(getBaseContext(), "check your email", Toast.LENGTH_LONG).show();

                            });
                        }
                        dialog.dismiss();

                    }).
                    setNegativeButton("Cancel", (dialog, which) -> dialog.cancel()).show();

        });

        login.setOnClickListener(v -> {
            loginUser();
        });



    }

    private void loginUser() {
        String txt_email = email.getText().toString();
        String txt_password = password.getText().toString();

        if (txt_email.isEmpty()) {
            email.setError("Enter Your Email");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(txt_email).matches()) {
            email.setError("Enter Valid Email");
            email.requestFocus();
            return;
        }
        if (txt_password.isEmpty()) {
            password.setError("Enter Your Password");
            password.requestFocus();
            return;
        }
        login.setEnabled(false);

        //SignIn with email and password
        auth.signInWithEmailAndPassword(txt_email, txt_password).addOnSuccessListener(authResult -> {
            //If teh user get into his deleted account this will remove his account from the FirebaseAuth
            CheckUser();

        }).addOnFailureListener(e -> {

            login.setEnabled(true);
            Toast.makeText(login.this, e.getMessage(), Toast.LENGTH_LONG).show();

        });

    }

    private void CheckUser() {

        UserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                chefReference = snapshot.child("Chef").child(auth.getUid()).exists();
                clientReference = snapshot.child("Client").child(auth.getUid()).exists();

                 if (clientReference) {
                     Toast.makeText(login.this, "Your are a client , Welcome", Toast.LENGTH_LONG).show();
                     login.setEnabled(true);

//                    Intent i = new Intent(login.this, ClientMainActivity.class);
//                    startActivity(i);
//                    finish();
                } else if (chefReference) {
                     Toast.makeText(login.this, "Your are a chef welcome", Toast.LENGTH_LONG).show();
                     login.setEnabled(true);

                    Intent i = new Intent(login.this, ChefActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(login.this, "Your don't have account", Toast.LENGTH_LONG).show();
                    login.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("database error", error.getMessage());
            }
        });


    }


}