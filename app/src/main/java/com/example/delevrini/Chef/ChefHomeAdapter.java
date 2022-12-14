package com.example.delevrini.Chef;


import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.delevrini.Food;
import com.example.delevrini.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

public class ChefHomeAdapter extends RecyclerView.Adapter<ChefHomeAdapter.FoodViewHolder> {

    private Context context;
    private ArrayList<Food> foodlist;
    private FirebaseAuth auth;
    private AlertDialog.Builder alertDialog;
    private DatabaseReference foodDatabase;
    private StorageReference storageRef;
    public static String foodId;

    public ChefHomeAdapter(Context context, ArrayList<Food> foodlist) {
        this.context = context;
        this.foodlist = foodlist;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.row_chef_food, parent, false);

        return new FoodViewHolder(v);
    }



    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {

        Food food = foodlist.get(position);

        foodDatabase = FirebaseDatabase.getInstance().getReference("Food");
        alertDialog = new AlertDialog.Builder(context);


        auth = FirebaseAuth.getInstance();

        Picasso.get().load(food.getImageLink().get(0)).resize(2048, 1600)
                .onlyScaleDown().placeholder(R.drawable.icon1).into(holder.Image);
        holder.Title.setText(food.getTitle());
        holder.Location.setText(food.getLocation());
        holder.Price.setText(food.getPrice());


        holder.editBtn.setOnClickListener(view -> {

            Intent intent = new Intent("broadcast food");
            intent.putExtra("food id", food.getIdFood());
            intent.putExtra("food idOwner", food.getIdowner());
            intent.putExtra("food Title", food.getTitle());
            intent.putExtra("food location", food.getLocation());
            intent.putExtra("food latitude", food.getLatitude());
            intent.putExtra("food longitude", food.getLongitude());
            intent.putExtra("food price", food.getPrice());
            intent.putExtra("food description", food.getDescription());
            intent.putExtra("food imageLinks", food.getImageLink());
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            foodId = food.getIdFood();

            //TODO Menu
            replace(new ChefEdit());

        });
        holder.deleteBtn.setOnClickListener(view -> alertDialog.setMessage("Are you sure you want to delete This Post ?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialogInterface, i) -> {

                    storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://delivrini-9e48f.appspot.com").child("images/").child(food.getIdowner());    //change the url acco
                    // delete image from firebase storage
                    Log.e("food id ",food.getTitle());
                    storageRef.child(food.getIdFood()).listAll().addOnSuccessListener(listResult -> {
                        for (StorageReference result : listResult.getItems()) {
                            result.delete();
                        }
                    }).addOnCompleteListener(task -> {
                        Toast.makeText(context, " Post Images Deleted ", Toast.LENGTH_SHORT).show();
                        // delete user from realtime database
                        foodDatabase.child(food.getIdFood()).removeValue().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                //TODO Menu
                                replace(new ChefHome());

                                Toast.makeText(context, " Your post was deleted ", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Delete Error" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

                    });
                })
                .setNegativeButton("No", (dialogInterface, i) -> {
                    dialogInterface.dismiss();

                }).show());
//        holder.rowOwnerFood.setOnClickListener(view -> {
//            // Send food information to the SingleFoodActivity
//            Intent i = new Intent(context, OwnerSingleFood.class);
//            i.putExtra("IdOwner", food.getIdowner());
//            i.putExtra("IdFood", food.getIdfood());
//            i.putExtra("Title", food.getTitle());
//            i.putExtra("Price", food.getPrice());
//            i.putExtra("Location", food.getLocation());
//            i.putExtra("Rooms", food.getRooms());
//            i.putExtra("Description", food.getDescription());
//            i.putExtra("ImageLink", food.getImageLink());
//
//            // Shared Food adapter image with Single food  Activity image
//            ActivityOptions transitionAnimation = ActivityOptions
//                    .makeSceneTransitionAnimation((Activity) context, view.findViewById(R.id.ownerfoodimagecard), "foodOwnerImage");
//            context.startActivity(i, transitionAnimation.toBundle());
//        });

    }

    @Override
    public int getItemCount() {
        return foodlist.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {

        private ImageView Image;
        private TextView Title, Price, Location, Room;
        private Button editBtn, deleteBtn;
        private LinearLayout rowOwnerFood;


        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);

            Image = itemView.findViewById(R.id.ownerpropertyimage);
            Title = itemView.findViewById(R.id.ownerpropertytitle);
            Price = itemView.findViewById(R.id.ownerprice);
            Location = itemView.findViewById(R.id.ownerpropertylocation);
            Room = itemView.findViewById(R.id.ownerroom);
            editBtn = itemView.findViewById(R.id.editPropertyBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            rowOwnerFood = itemView.findViewById(R.id.rowOwnerProperty);

        }
    }

    private void replace(Fragment fragment) {

        FragmentTransaction transaction = ((ChefActivity) context).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();

    }


}


