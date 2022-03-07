package com.wetwo.librarymanagment.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wetwo.librarymanagment.R;
import com.wetwo.librarymanagment.data.model.ImageUploadInfo;
import com.wetwo.librarymanagment.utils.OnClickListener;

import java.util.List;

public class bookListingAdapter extends RecyclerView.Adapter<bookListingAdapter.ViewHolder> {

    Context context;
    List<ImageUploadInfo> MainImageUploadInfoList;
    Boolean isAdmin;
    OnClickListener listener;


    public bookListingAdapter(Context context, List<ImageUploadInfo> TempList, Boolean isAdmin, OnClickListener listener) {


        this.MainImageUploadInfoList = TempList;

        this.context = context;

        this.isAdmin = isAdmin;

        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_listing_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ImageUploadInfo UploadInfo = MainImageUploadInfoList.get(position);
        Log.e("data", "" + UploadInfo.imageURL);


        //set data to textview
        holder.imageNameTextView.setText(UploadInfo.getBookName());
        holder.txtBookAuther.setText("Author : " + UploadInfo.getBookAuthor());
        holder.txtBookType.setText("Category : " + UploadInfo.getBookSub());
        if (UploadInfo.isBookAvailable) {
            holder.bookAvailable.setText("Book is Available");
            holder.bookAvailable.setTextColor(Color.BLUE);
        } else {
            holder.bookAvailable.setText("Book is not  Available");
            holder.bookAvailable.setTextColor(Color.RED);
        }
        if (isAdmin)
            holder.requestButton.setVisibility(View.GONE);
        else
            holder.requestButton.setVisibility(View.VISIBLE);

        holder.requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position);
            }
        });

        StorageReference mImageStorage = FirebaseStorage.getInstance().getReference();
        StorageReference ref = mImageStorage.child("library_book/")
                .child(UploadInfo.getImageName());

        ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downUri = task.getResult();
                    String imageUrl = downUri.toString();

                    Glide.with(context).load(imageUrl)

                            .error(R.drawable.ic_baseline_broken_image_24)
                            .into(holder.imageView);
                    Log.e("data", "" + imageUrl);
                } else {
                    Log.e(" no data", "" + task.getException());

                }
            }
        });

    }

    @Override
    public int getItemCount() {

        return MainImageUploadInfoList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView imageNameTextView, txtBookAuther, txtBookType, bookAvailable;
        public MaterialButton requestButton;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imageView);

            imageNameTextView = (TextView) itemView.findViewById(R.id.ImageNameTextView);
            txtBookAuther = (TextView) itemView.findViewById(R.id.txt_author);
            txtBookType = (TextView) itemView.findViewById(R.id.txt_category);
            bookAvailable = (TextView) itemView.findViewById(R.id.txt_available);
            requestButton = (MaterialButton) itemView.findViewById(R.id.btn_request);
        }
    }
}