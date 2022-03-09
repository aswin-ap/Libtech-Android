package com.wetwo.librarymanagment.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.wetwo.librarymanagment.utils.OnItemClickListener;

import java.util.List;

public class myRequestAdapter extends RecyclerView.Adapter<myRequestAdapter.ViewHolder> {

    Context context;
    List<ImageUploadInfo> MainImageUploadInfoList;
    Boolean isAdmin;
    OnItemClickListener listener;


    public myRequestAdapter(Context context, List<ImageUploadInfo> TempList, Boolean isAdmin, OnItemClickListener listener) {


        this.MainImageUploadInfoList = TempList;

        this.context = context;

        this.isAdmin = isAdmin;

        this.listener = listener;
    }

    @Override
    public myRequestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_book_req_layout, parent, false);

        myRequestAdapter.ViewHolder viewHolder = new myRequestAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(myRequestAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ImageUploadInfo UploadInfo = MainImageUploadInfoList.get(position);
        Log.e("data isRequest ", "" + UploadInfo.isRequest);


        //set data to textview
        holder.imageNameTextView.setText(UploadInfo.getBookName());
        holder.txtBookAuther.setText("Author : " + UploadInfo.getBookAuthor());
        holder.txtBookType.setText("Category : " + UploadInfo.getBookSub());

        if (UploadInfo.getStatus().equals("request")) {
            holder.requestButton.setVisibility(View.VISIBLE);
            holder.tvAlreadyRequested.setVisibility(View.GONE);
        } else {
            holder.requestButton.setVisibility(View.GONE);
            holder.tvAlreadyRequested.setVisibility(View.VISIBLE);
        }

        holder.requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position, UploadInfo.getBookBuyer());
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
        public TextView imageNameTextView, txtBookAuther, txtBookType, tvAlreadyRequested;
        public MaterialButton requestButton;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imageView);

            imageNameTextView = (TextView) itemView.findViewById(R.id.ImageNameTextView);
            txtBookAuther = (TextView) itemView.findViewById(R.id.txt_author);
            txtBookType = (TextView) itemView.findViewById(R.id.txt_category);

            requestButton = (MaterialButton) itemView.findViewById(R.id.btn_request);
            tvAlreadyRequested = (TextView) itemView.findViewById(R.id.tv_already_requested);
        }
    }
}