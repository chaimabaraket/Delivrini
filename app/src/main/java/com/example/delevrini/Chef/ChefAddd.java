package com.example.delevrini.Chef;

import static android.app.Activity.RESULT_OK;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.delevrini.Food;
import com.example.delevrini.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.HttpsURLConnection;


public class ChefAddd extends Fragment {

    private EditText title, price, description;
    private Spinner location;
    private Button addButton;
    private TextView addPictureBtn;
    private DatabaseReference database;
    private FirebaseAuth auth;
    private StorageReference storageRef;
    private Uri filePath;
    private ArrayList<Uri> imageUriList;
    private ArrayList<String> imagesName, linkImages, arraySpinner;
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private String longitudeString, latitudeString;

    // push database key for the property
    public static String idProperty;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_chef_addd, container, false);

        // Register receiver
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver,
                new IntentFilter("broadcast address"));

        database = FirebaseDatabase.getInstance().getReference("Food");
        auth = FirebaseAuth.getInstance();
        //Get storage reference
        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://delivrini-9e48f.appspot.com").child("images/").child(auth.getCurrentUser().getUid());    //change the url acco

        // Initialisation of animation
        title = v.findViewById(R.id.property_add_title);
        location = v.findViewById(R.id.property_add_location);
        price = v.findViewById(R.id.property_add_price);
        description = v.findViewById(R.id.property_add_description);
        addButton = v.findViewById(R.id.add_property_button);
        addPictureBtn = v.findViewById(R.id.property_add_picture);
        recyclerView = v.findViewById(R.id.recycleview_images);
        // recycler view settings
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext(), LinearLayoutManager.HORIZONTAL, false));
        // list of uri images  from gallery declaration
        imageUriList = new ArrayList<>();
        // Setup the list in the adapter
        imageAdapter = new ImageAdapter(v.getContext(), imageUriList);
        recyclerView.setAdapter(imageAdapter);

        imagesName = new ArrayList<>();

        // Spinner touch listener , if is empty will open map fragment
        location.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (arraySpinner == null) {
                    replace(new MapFragment());
                }
            }
            return false;
        });
        // Spinner item  clicked listener, if click on search will open map fragment
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
        //disable add button until image added
        addButton.setEnabled(false);
        //Handle the click on the add button
        handleAddClick();

        return  v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data.getClipData() != null) {


            int count = data.getClipData().getItemCount();
            for (int i = 0; i < count; i++) {
                Log.e("image data", String.valueOf(data.getClipData()));

                filePath = data.getClipData().getItemAt(i).getUri();
                imageUriList.add(filePath);
                // setting the image name from the gallery
                imagesName.add(filePath.getLastPathSegment() + ".jpg");
                imageAdapter.notifyDataSetChanged();
            }


            // set the add button visible if image selected from the gallery ( if the there is an image path)
            addButton.setEnabled(true);

        } else {
            Toast.makeText(getContext(), "Select an image", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleAddClick() {
        addPictureBtn.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(intent, "Select Image"), 2);

        });
        addButton.setOnClickListener(view -> {
//            if (isInternetAvailable()) {
                //disable add button until post successfully added cli
                addFood();
//            } else {
//                Toast.makeText(getContext(), "Check Your Connexion", Toast.LENGTH_SHORT).show();
//            }
        });

    }

    private void addFood() {

        String titleTxt = title.getText().toString();
        String descriptionTxt = description.getText().toString();
        String locationTxt = (String) location.getSelectedItem();
        String priceTxt = price.getText().toString();
        idProperty = database.push().getKey();


        if (titleTxt.trim().isEmpty()) {
            title.setError(" Forget to set a title");
            title.requestFocus();
            return;
        }

        if (priceTxt.isEmpty()) {
            price.setError("Forget to set property price");
            price.requestFocus();
            return;
        }


        AtomicInteger endCount = new AtomicInteger();
        linkImages = new ArrayList<>();
        for (int i = 0; i < imageAdapter.getItemCount(); i++) {
            // uploading the image to the storage if property data added to the database
            final StorageReference ref = storageRef.child(idProperty).child(imagesName.get(i)); // saving image in folder with  post id and under the name of the image
            UploadTask uploadTask = ref.putFile(imageUriList.get(i));

            uploadTask.addOnProgressListener(snapshot -> {
                int progress = (int) ((100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount());
                });
                // Test if the picture added successfully
                uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        // if upload didn't finish
                        Toast.makeText(getContext(), "Couldn't upload your post", Toast.LENGTH_LONG).show();
                }
                // Continue with the task to get the download URL
                return ref.getDownloadUrl();

            }).addOnCompleteListener(task -> {
                // Handle unpicked image exception
                try {

                    if (task.isSuccessful()) {
                        // Getting the downloaded URL
                        linkImages.add(task.getResult().toString());
                        endCount.getAndIncrement();

                        if (endCount.get() == imageAdapter.getItemCount()) {
                            //TODO set the latitude  & the langtiture & the address
                            latitudeString = "525545214";
                            longitudeString = "525545214";

                            // Create an instance of the Property class
                            Food food = new Food(auth.getUid(), idProperty, titleTxt, locationTxt, latitudeString, longitudeString, descriptionTxt, priceTxt, linkImages);

                            // adding property information to the database
                            database.child(idProperty).setValue(food).addOnSuccessListener(aVoid -> {
                                // if image was successfully uploaded will return to the home page
                                replace(new ChefHome());
                                Toast.makeText(getContext(), "Add Successfully", Toast.LENGTH_LONG).show();

                            }).addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed To Add Your Post", Toast.LENGTH_LONG).show();
                            });


                        }
                    } else {
                        // Handle failures
                        Toast.makeText(getContext(), "Failed To Upload images The Post", Toast.LENGTH_LONG).show();
                        addButton.setEnabled(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Address address = intent.getParcelableExtra("addresses");
            String addressName = address.getExtras().getString("display_name");

            latitudeString = String.valueOf(address.getLatitude());
            longitudeString = String.valueOf(address.getLongitude());
            String adminArea = address.getAdminArea();
            String locality = address.getLocality();
            int end = addressName.indexOf(",");
            String subAdminArea = addressName.substring(0, end);

            arraySpinner = new ArrayList<>();
            arraySpinner.add("Search for location..");

            arraySpinner.add(0, adminArea + ", " + subAdminArea);
            if (locality != null) {
                arraySpinner.add(0, adminArea + ", " + locality + ", " + subAdminArea);
            }

            if (getContext() != null) {
                ArrayAdapter<String> myLocation = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_list_item_1, arraySpinner);
                location.setAdapter(myLocation);
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
