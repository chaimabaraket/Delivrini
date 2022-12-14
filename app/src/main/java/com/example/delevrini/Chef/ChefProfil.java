package com.example.delevrini.Chef;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.delevrini.R;
import com.example.delevrini.User;
import com.example.delevrini.login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


public class ChefProfil extends Fragment {

    private ImageView profileImg, clickLogo;
    private Button returnBtn, updateBtn, logoutBtn;
    private EditText username, password, email, phone;
    private TextView profileImgBtn;
    private FirebaseAuth auth;
    private StorageReference storageRef;
    private DatabaseReference chefDatabase;
    private ValueEventListener listener;
    private Uri filePath;
    private static String linkImage;
    private ProgressDialog pd;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // Create database event listener
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    username.setText(user.getUsername());
                    email.setText(user.getEmail());
                    phone.setText(user.getPhone());
//                    check if there is a link in the database
                    if (!user.getProfileImage().equals(" ")) {
                        Picasso.get().load(user.getProfileImage()).resize(2048, 1600)
                                .onlyScaleDown().placeholder(R.drawable.icon1).into(profileImg);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Database erreur", error.getMessage());
            }
        };

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View v = inflater.inflate(R.layout.fragment_chef_profil, container, false);


        // GETTING VIEWS FROM FRAGMENT
        username = v.findViewById(R.id.owner_Edit_Username);
        password = v.findViewById(R.id.owner_Edit_Password);
        email = v.findViewById(R.id.owner_Edit_Email);
        phone = v.findViewById(R.id.owner_Edit_Phone);
        profileImg = v.findViewById(R.id.profil_img);
        profileImgBtn = v.findViewById(R.id.profil_img_Btn);
        returnBtn = v.findViewById(R.id.owner_profil_return);
        updateBtn = v.findViewById(R.id.owner_Update_Btn);
        logoutBtn = v.findViewById(R.id.owner_Logout_Btn);
        clickLogo = v.findViewById(R.id.clickLogo);

        auth = FirebaseAuth.getInstance();
        String CurrentUser = auth.getCurrentUser().getUid();
        //Get storage reference
        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://delivrini-9e48f.appspot.com").child("images/").child(auth.getCurrentUser().getUid());    //change the url acco
        //Get Database reference
        chefDatabase = FirebaseDatabase.getInstance().getReference("Users").child("Chef");


        // Setting listener of the owner data from database
        chefDatabase.child(CurrentUser).addValueEventListener(listener);

        clickLogo.setOnClickListener(view -> {
            replace(new ChefHome());        });

        profileImgBtn.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent, "Select Image"), 2);
        });

        updateBtn.setOnClickListener(view -> {
            //Call the update function
            update(CurrentUser);
            Log.e("update btn", " this is brn update profile owner");
        });

        returnBtn.setOnClickListener(view -> {
            replace(new ChefHome());
        });

        logoutBtn.setOnClickListener(view -> {
                auth.signOut();
                Intent i = new Intent(getContext(), login.class);
                startActivity(i);
                getActivity().finish();


        });




         return v;
    }

    private void update(String CurrentUser) {

        String usernameTxt = username.getText().toString();
        String passwordTxt = password.getText().toString();
        String emailTxt = email.getText().toString();
        String phoneTxt = phone.getText().toString();

        if (usernameTxt.isEmpty()) {
            username.setError(" Need your name");
            username.requestFocus();
            return;
        }
        if (passwordTxt.isEmpty()) {
            password.setError("Need your password");
            password.requestFocus();
            return;
        }
        if (passwordTxt.length() < 6) {
            password.setError("Length of password is more than 6");
            password.requestFocus();
            return;
        }
        if (emailTxt.isEmpty()) {
            email.setError("Email is empty");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches()) {
            email.setError("Enter the valid email");
            email.requestFocus();
            return;
        }
        if (phoneTxt.isEmpty()) {
            phone.setError("Forget your phone number");
            phone.requestFocus();
            return;
        }
        if (phoneTxt.length() != 8) {
            phone.setError("Phone number have 8 numbers");
            phone.requestFocus();
            return;
        }
        if (!(phoneTxt.startsWith("5") || phoneTxt.startsWith("2") || phoneTxt.startsWith("9"))) {
            phone.setError("Enter a valid phone number");
            phone.requestFocus();
            return;
        }

            auth.getCurrentUser().updateEmail(emailTxt).addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Email updated!", Toast.LENGTH_LONG).show());
            auth.getCurrentUser().updatePassword(passwordTxt).addOnSuccessListener(aVoid -> Toast.makeText(getContext(), " Update done!", Toast.LENGTH_LONG).show());
            // adding the client updated information to the database
            chefDatabase.child(CurrentUser).child("username").setValue(usernameTxt);
            chefDatabase.child(CurrentUser).child("email").setValue(emailTxt);
            chefDatabase.child(CurrentUser).child("phone").setValue(phoneTxt);
            Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();

    }

    private void replace(Fragment fragment) {

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();

    }

}