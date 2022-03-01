package com.wetwo.librarymanagment.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wetwo.librarymanagment.R;
import com.wetwo.librarymanagment.data.model.ImageUploadInfo;
import com.wetwo.librarymanagment.ui.book.ListBooksActivity;

import java.util.List;

public class bookListingAdapter extends RecyclerView.Adapter<bookListingAdapter.ViewHolder> {

    Context context;
    List<ImageUploadInfo> MainImageUploadInfoList;


    public bookListingAdapter(Context context, List<ImageUploadInfo> TempList) {


        this.MainImageUploadInfoList = TempList;

        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_listing_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageUploadInfo UploadInfo = MainImageUploadInfoList.get(position);
        Log.e("data", "" + UploadInfo.imageURL);


        //set data to textview
        holder.imageNameTextView.setText(UploadInfo.getBookName());
        holder.txtBookAuther.setText("Author : " + UploadInfo.getBookAuthor());
        holder.txtBookType.setText("Category : " + UploadInfo.getBookSub());


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
        public TextView imageNameTextView, txtBookAuther, txtBookType;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imageView);

            imageNameTextView = (TextView) itemView.findViewById(R.id.ImageNameTextView);
            txtBookAuther = (TextView) itemView.findViewById(R.id.txt_author);
            txtBookType = (TextView) itemView.findViewById(R.id.txt_category);
        }
    }
}