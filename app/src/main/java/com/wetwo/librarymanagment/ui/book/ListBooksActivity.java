package com.wetwo.librarymanagment.ui.book;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wetwo.librarymanagment.R;
import com.wetwo.librarymanagment.adapter.bookListingAdapter;
import com.wetwo.librarymanagment.data.model.ImageUploadInfo;
import com.wetwo.librarymanagment.databinding.ActivityAddBookBinding;
import com.wetwo.librarymanagment.databinding.ActivityListBooksBinding;
import com.wetwo.librarymanagment.ui.LoginActivity;
import com.wetwo.librarymanagment.ui.SplashActivity;

import java.util.ArrayList;
import java.util.List;

public class ListBooksActivity extends AppCompatActivity {
    private ActivityListBooksBinding binding;
    String Storage_Path = "library_book/";
    // Root Database Name for Firebase Database.
    String Database_Path = "All_Image_Uploads_Database";

    // Creating StorageReference and DatabaseReference object.
    StorageReference storageReference;
    DatabaseReference databaseReference;

    ProgressDialog progressDialog;
    // Creating List of ImageUploadInfo class.
    List<ImageUploadInfo> list = new ArrayList();
    // Creating RecyclerView.Adapter.
    RecyclerView.Adapter adapter ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListBooksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Assign FirebaseStorage instance to storageReference.
        storageReference = FirebaseStorage.getInstance().getReference();

        // Assign FirebaseDatabase instance with root database name.
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        list.clear();
        getAllBooks();

        btnClick();
    }

    private void btnClick() {
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ListBooksActivity.this,
                        AddBookActivity.class);

                startActivity(i);
            }
            }
        );
    }

   private void getIm(String img_name){
       StorageReference mImageStorage = FirebaseStorage.getInstance().getReference();
       StorageReference ref = mImageStorage.child(Storage_Path)
               .child(img_name);

       ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
           @Override
           public void onComplete(@NonNull Task<Uri> task) {
               if (task.isSuccessful()) {
                   Uri downUri = task.getResult();
                   String imageUrl = downUri.toString();
                   Toast.makeText(ListBooksActivity.this, imageUrl , Toast.LENGTH_SHORT).show();
               }else{
                   Toast.makeText(ListBooksActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
               }
           }
       });
   }


        private void getAllBooks() {

        // Setting up Firebase image upload folder path in databaseReference.
        // The path is already defined in MainActivity.
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);

        // Adding Add Value Event Listener to databaseReference.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    ImageUploadInfo imageUploadInfo = postSnapshot.getValue(ImageUploadInfo.class);

                    list.add(imageUploadInfo);
                }

                Log.e("itt", "" + list);
                adapter = new bookListingAdapter(getApplicationContext(), list);

                binding.recyclerView.setAdapter(adapter);

                // Hiding the progress dialog.
//                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("itt", "" + databaseError);
                // Hiding the progress dialog.
                progressDialog.dismiss();

            }
        });
    }
}