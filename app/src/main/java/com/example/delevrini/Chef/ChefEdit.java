package com.example.delevrini.Chef;

import static com.example.delevrini.Chef.ChefHomeAdapter.foodId;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.delevrini.Food;
import com.example.delevrini.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ChefEdit extends Fragment {
    private EditText title, price, description;
    private Spinner location;
    private Button editBtn;
    private DatabaseReference foodDatabase;
    private FirebaseAuth auth;
    private String longitudeString, latitudeString, longitudeMap, latitudeMap;
    private ArrayList<String> arraySpinner;
    private ArrayAdapter<String> myLocation;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public ChefEdit() {
        // empty constructor is required
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chef_edit, container, false);
// Register map data receiver
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver,
                new IntentFilter("broadcast address"));


        // Instance of firebase
        auth = FirebaseAuth.getInstance();
        foodDatabase = FirebaseDatabase.getInstance().getReference("Food").child(foodId);
        // View Initialisation
        title = v.findViewById(R.id.property_edit_title);
        location = v.findViewById(R.id.property_edit_location);
        price = v.findViewById(R.id.property_edit_price);
        description = v.findViewById(R.id.property_edit_description);
        editBtn = v.findViewById(R.id.edit_property_button);


        arraySpinner = new ArrayList<>();

        myLocation = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, arraySpinner);

        foodDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Food food = snapshot.getValue(Food.class);
                assert food != null;
                title.setText(food.getTitle());
                // get only the number of rooms without s+
                myLocation.add(food.getLocation());
                arraySpinner.add("Search for location..");
                location.setAdapter(myLocation);
                price.setText(food.getPrice());
                description.setText(food.getDescription());
                longitudeString = food.getLongitude();
                latitudeString = food.getLatitude();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        location.setOnTouchListener((v1, event) -> {
            if (v1.performClick()) {
                if (arraySpinner == null) {
                    replace(new MapFragment());
                }
            }
            return false;
        });
        location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (arraySpinner != null) {
                    if (i == arraySpinner.size() - 1) {
                        replace(new MapFragment());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        // Handle Edit button click
        clickHandler();
        return v ;
    }

    private void clickHandler() {


        // on click the edit button to confirm the updates
        editBtn.setOnClickListener(view -> {

            String titleTxt = title.getText().toString();
            String locationTxt = (String) location.getSelectedItem();
            String descriptionTxt = description.getText().toString();
            String priceTxt = price.getText().toString();

            if (titleTxt.isEmpty()) {
                title.setError("Forget to set a title ");
                title.requestFocus();
                return;
            }

            if (priceTxt.isEmpty()) {
                price.setError("Forget to set property price");
                price.requestFocus();
                return;
            }



                foodDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        foodDatabase.child("title").setValue(titleTxt);
                        foodDatabase.child("location").setValue(locationTxt);
                        foodDatabase.child("price").setValue(priceTxt);
                        foodDatabase.child("description").setValue(descriptionTxt);
                        //TODO
                        // if owner change location of the restaurant the data from the map will take place
//                        if (latitudeMap == null) {
//                            foodDatabase.child("longitude").setValue(longitudeString);
//                            foodDatabase.child("latitude").setValue(latitudeString);
//                        } else {
//                            foodDatabase.child("longitude").setValue(latitudeMap);
//                            foodDatabase.child("latitude").setValue(longitudeMap);
//                        }
                        replace(new ChefHome());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        });

    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Address address = intent.getParcelableExtra("addresses");
            String addressName = address.getExtras().getString("display_name");

            latitudeMap = String.valueOf(address.getLatitude());
            longitudeMap = String.valueOf(address.getLongitude());

            String adminArea = address.getAdminArea();
            String locality = address.getLocality();
            int end = addressName.indexOf(",");
            String subAdminArea = addressName.substring(0, end);
            // clear the last add if returned to pick another location
            myLocation.clear();

            arraySpinner.add(0, adminArea + ", " + subAdminArea);
            if (locality != null) {
                arraySpinner.add(0, adminArea + ", " + locality + ", " + subAdminArea);
            }

            if (getContext() != null) {
                // notify the adapter of the changes in the array to appear
                myLocation.notifyDataSetChanged();
            }
        }
    };


    private void replace(Fragment fragment) {

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();

    }


}