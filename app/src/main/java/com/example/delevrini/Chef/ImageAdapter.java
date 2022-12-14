package com.example.delevrini.Chef;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.delevrini.R;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageAdapterViewHolder> {
    private Context context;
    private ArrayList<Uri> imageUri;


    public ImageAdapter(Context context, ArrayList<Uri> imageUri) {
        this.context = context;
        this.imageUri = imageUri;


    }


    @NonNull
    @Override
    public ImageAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_image_list, parent, false);
        return new ImageAdapterViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapterViewHolder holder, int position) {
        Uri uri = imageUri.get(position);

        holder.image.setImageURI(uri);
        holder.deleteImage.setOnClickListener(view -> {
            imageUri.remove(uri);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return imageUri.size();    }

    public static class ImageAdapterViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        ImageButton deleteImage;

        public ImageAdapterViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.row_list_images);
            deleteImage = itemView.findViewById(R.id.deleteImage_btn);
        }
    }
}
