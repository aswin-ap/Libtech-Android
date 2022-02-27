package com.wetwo.librarymanagment.ui.book;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
//        getAllBooks();
        getgg();
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
    private void getgg(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("Blog").child(uid);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uid = dataSnapshot.child("uid").getValue(String.class);
                String title = dataSnapshot.child("title").getValue(String.class);
                String timestamp = dataSnapshot.child("timestamp").getValue(String.class);
                Log.d("TAG", uid + " / " + title  + " / " + timestamp);

                for(DataSnapshot ds : dataSnapshot.child("url").getChildren()) {
                    String url = ds.getValue(String.class);
                    Log.d("TAG", url);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);
    }

    private void getAllBooks() {

        // Setting up Firebase image upload folder path in databaseReference.
        // The path is already defined in MainActivity.
        databaseReference = FirebaseDatabase.getInstance().getReference(Storage_Path);

        // Adding Add Value Event Listener to databaseReference.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

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