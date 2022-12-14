package com.example.delevrini.Chef;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.delevrini.Food;
import com.example.delevrini.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ChefHome extends Fragment {

    private DatabaseReference fooddatabase, UserDatabase;
    private ValueEventListener foodListener;
    private RecyclerView recyclerView;
    private ChefHomeAdapter ChefHomeAdapter;
    private ArrayList<Food> list;
    private LinearLayout addview;
    private ImageView add;
    private TextView ownerPlaceHolder;
    private FirebaseAuth auth;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);


        foodListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot snapshotChild : snapshot.getChildren()) {

//                    Log.e("liste property", (String) snapshotChild.child("idowner").getValue());


                    // check from the owner of the property

                    if (snapshotChild.child("idowner").getValue().equals(auth.getUid())) {
                        Log.e("liste property", "String.valueOf(property)");


                        String idowner = (String) snapshotChild.child("idowner").getValue();
                        String idFood = (String) snapshotChild.child("idFood").getValue();
                        String title = (String) snapshotChild.child("title").getValue();
                        String location = (String) snapshotChild.child("location").getValue();
                        String longitude = (String) snapshotChild.child("longitude").getValue();
                        String latitude = (String) snapshotChild.child("latitude").getValue();
                        String description = (String) snapshotChild.child("description").getValue();
                        String price = (String) snapshotChild.child("price").getValue();
                        // getting image link list
                        ArrayList<String> imageLink = new ArrayList<>();
                        for (DataSnapshot snapshot1 : snapshotChild.child("imageLink").getChildren()) {
                            imageLink.add((String) snapshot1.getValue());

                        }
//                        get properties from database and set in Property instance
                        Food property = new Food(idowner, idFood, title, location,longitude,latitude, description, price, imageLink);
                        //add property to recycle view in the first position
                        list.add(0, property);

                        //notifyItemInserted(list.indexOf(property));
                        ChefHomeAdapter.notifyDataSetChanged();
                        Log.e("notify","im here");
                    }
                }
                // if there is no post add button will appearA
                if (list.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    addview.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
        View v = inflater.inflate(R.layout.fragment_chef_home, container, false);

        recyclerView = v.findViewById(R.id.property_owner_list);
        ownerPlaceHolder = v.findViewById(R.id.ownerPlaceHolder);
        addview = v.findViewById(R.id.addview);
        add = v.findViewById(R.id.addbutton);
        // RecyclerView list parameter
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        // get the firebase instance
        auth = FirebaseAuth.getInstance();
        UserDatabase = FirebaseDatabase.getInstance().getReference("Users");
        fooddatabase = FirebaseDatabase.getInstance().getReference("Food");

        // set the list to adapter of the recycler view
        list = new ArrayList<>();
        ChefHomeAdapter = new ChefHomeAdapter(v.getContext(), list);
        recyclerView.setAdapter(ChefHomeAdapter);
        // Call the values from the firebase database
        ValueListener();

        // If there is no data an add  button will appear to click and add some new data
        add.setOnClickListener(view -> {
            replace(new ChefAddd());
        });



        return v;
    }

    private void ValueListener() {
        // Setting Listener on  property data from firebase realtime database
        fooddatabase.addListenerForSingleValueEvent(foodListener);
    }

    private void replace(Fragment fragment) {

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();

    }

}