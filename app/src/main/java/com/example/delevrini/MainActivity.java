package com.example.delevrini;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NO_HISTORY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.delevrini.Chef.ChefActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private View decorview;
    private FirebaseAuth auth;
    private DatabaseReference userReference;
    private Boolean adminReference, ownerReference, clientReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //hide system bars
        decorview = getWindow().getDecorView();
        decorview.setSystemUiVisibility(hideSystemBars());

        //get current user with firebase auth
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference("Users");
        ImageView img = findViewById(R.id.fulllogo);



        // make new animation : get the ressource animation
        Animation fade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);

        //set animation for our image
        img.startAnimation(fade);

        //delay for 2sec to show the SplashScreen before running the functions
        //This method will be executed once the timer is over
        new Handler().postDelayed(() -> {

            // check if there is a logged User
                if (user != null) {

                    //if we a user is already logged in ,this listener will check if it's exists in the database of users
                    userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            ownerReference = snapshot.child("Chef").child(user.getUid()).exists();
                            clientReference = snapshot.child("Client").child(user.getUid()).exists();


                                //check if user UID exist in the Owner table of Users
                                if (ownerReference) {
                                Log.e("Im in ", "owner if in splash screen");

                                Intent i = new Intent(MainActivity.this, ChefActivity.class);
                                i.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                finish();
                                //check if user UID exist in the Client table of Users
                            }
                                //TODO Client Interface
//                                else if (clientReference) {
//                                Intent i = new Intent(MainActivity.this, ClientMainActivity.class);
//                                i.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(i);
//                                finish();
//                            }
                                else {
                                // if user was deleted from database
                                try {
                                    auth.getCurrentUser().delete();
                                    auth.signOut();
                                    Intent i = new Intent(MainActivity.this, login.class);
                                    // Make the new intent the first activity
                                    i.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_NO_HISTORY);
                                    startActivity(i);
                                    finish();
                                    Toast.makeText(getBaseContext(), "Didn't found your  Account! ", Toast.LENGTH_SHORT).show();
                                } catch (NullPointerException e) {

                                    Toast.makeText(getBaseContext(), " Your Account was deleted", Toast.LENGTH_LONG).show();

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    // if there is no  logged User the application will start in the login activity
                } else {
                    Intent i = new Intent(MainActivity.this, login.class);
                    // Make the new intent the first activity
                    i.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    // Shared Splash screen Logo  and background with Login Activity small logo image and the blue bg

                    Pair bg = Pair.create(this.findViewById(R.id.splashscreen), "bg");
                    Pair logo = Pair.create(this.findViewById(R.id.fulllogo), "Logo");
                    ActivityOptions transitionAnimation = ActivityOptions
                            .makeSceneTransitionAnimation(this, bg, logo);
                    startActivity(i, transitionAnimation.toBundle());
                    finish();
                }



        }, 2000);
    }

    // To have all the system bar and set the app on full screen view Function
    private int hideSystemBars() {
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }
}
