package com.example.delevrini;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signin extends AppCompatActivity {

    private Button signup;
    private TextView textlogin;
    private EditText inputUsername, inputPassword, inputEmail, inputPhone;
    private Spinner userType;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        signup = findViewById(R.id.signup);
        inputUsername = findViewById(R.id.input_Username);
        inputEmail = findViewById(R.id.input_Email);
        inputPassword = findViewById(R.id.input_Password);
        inputPhone = findViewById(R.id.input_Phone);
        userType = findViewById(R.id.userType);
        textlogin = findViewById(R.id.text_login);
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        signup.setOnClickListener(v -> SignupUser());

    }

    private void SignupUser() {
        String input_username = inputUsername.getText().toString();
        String input_email = inputEmail.getText().toString();
        String input_password = inputPassword.getText().toString();
        String input_phone = inputPhone.getText().toString();
        String usertype = userType.getSelectedItem().toString();


        if (input_username.isEmpty()) {
            inputUsername.setError(" Need your name");
            inputUsername.requestFocus();
            return;
        }
        if (input_email.isEmpty()) {
            inputEmail.setError("Email is empty");
            inputEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(input_email).matches()) {
            inputEmail.setError("Enter the valid email");
            inputEmail.requestFocus();
            return;
        }
        if (input_password.isEmpty()) {
            inputPassword.setError("Password is empty");
            inputPassword.requestFocus();
            return;
        }
        if (input_password.length() < 6) {
            inputPassword.setError("Password is is too short");
            inputPassword.requestFocus();
            return;
        }
        if (input_phone.isEmpty()) {
            inputPhone.setError("Need your phone number");
            inputPhone.requestFocus();
            return;
        }

        if (input_phone.length() != 8) {
            inputPhone.setError("Phone number have 8 numbers");
            inputPhone.requestFocus();
            return;
        }
        if (!(input_phone.startsWith("5") || input_phone.startsWith("2") || input_phone.startsWith("9"))) {
            inputPhone.setError("Enter a valide phone number");
            inputPhone.requestFocus();
            return;
        }
        signup.setEnabled(false);
        auth.createUserWithEmailAndPassword(input_email, input_password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        User user = new User(auth.getCurrentUser().getUid(), input_username, input_email, usertype, input_phone," ");

                        mDatabase.child(usertype)
                                .child(auth.getCurrentUser().getUid())
                                .setValue(user).addOnCompleteListener(task1 -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(signin.this, "  Successfuly Signed ", Toast.LENGTH_LONG).show();
                                        // Redirect to the user MainActivity ("check which user is signed in );
                                        CheckUser();

                                    } else {
                                        Toast.makeText(signin.this, " Failed to add you  ", Toast.LENGTH_LONG).show();
                                        signup.setEnabled(true);
                                    }

                                });
                    } else {
                        Toast.makeText(signin.this, " Email is Used before try another one! ", Toast.LENGTH_LONG).show();
                        signup.setEnabled(true);
                    }
                });

    }

    private void CheckUser() {
        String usertype = userType.getSelectedItem().toString();

        if (usertype.equals("Client")) {

            Intent i = new Intent(signin.this, login.class);
            i.addFlags(FLAG_ACTIVITY_NEW_TASK|FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();

        } else if (usertype.equals("Chef")) {

            Intent i = new Intent(signin.this, login.class);
            i.addFlags(FLAG_ACTIVITY_NEW_TASK|FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();


        }

    }

}