package com.wetwo.librarymanagment.ui.book;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wetwo.librarymanagment.BaseActivity;
import com.wetwo.librarymanagment.data.model.ImageUploadInfo;
import com.wetwo.librarymanagment.databinding.ActivityAddBookBinding;

public class AddBookActivity extends BaseActivity {
    private ActivityAddBookBinding binding;
    Uri imageUri;
    // Folder path for Firebase Storage.
    String Storage_Path = "library_book/";
    // Root Database Name for Firebase Database.
    String Database_Path = "All_Image_Uploads_Database";

    // Creating StorageReference and DatabaseReference object.
    StorageReference storageReference;
    DatabaseReference databaseReference;

    ProgressDialog progressDialog;
    // Creating List of ImageUploadInfo class.
    List<ImageUploadInfo> list = new ArrayList();
    int size = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        size = getIntent().getIntExtra("id",0);
        Log.e("id", String.valueOf(size));
        // Assign FirebaseStorage instance to storageReference.
        storageReference = FirebaseStorage.getInstance().getReference();

        // Assign FirebaseDatabase instance with root database name.
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);

        // Assigning Id to ProgressDialog.
        progressDialog = new ProgressDialog(AddBookActivity.this);


        btnClick();
    }

    private void btnClick() {
        binding.BookImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                //setting the intent action to get content
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                //setting the upload content type as image
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 2);
            }
        });

        binding.AddBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadImageFileToFirebaseStorage();
            }
        });
        binding.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


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


                        // Hiding the progress dialog.
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("itt", "" + databaseError);
                        // Hiding the progress dialog.
                        progressDialog.dismiss();

                    }
                });
            }
        });
    }

    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {

            //Getting the image from the device and setting the image to imageView
            imageUri = data.getData();
            binding.BookImage.setImageURI(imageUri);

            File file = new File(imageUri.getPath());
            Log.e("img name", "" + imageUri);
            Log.e("img name", "" + file.getName());

        }
    }

    // Creating UploadImageFileToFirebaseStorage method to upload image on storage.
    public void UploadImageFileToFirebaseStorage() {

        // Checking whether FilePathUri Is empty or not.
        if (imageUri != null) {

            // Setting progressDialog Title.
            progressDialog.setTitle("Image is Uploading...");

            // Showing progressDialog.
            progressDialog.show();

            // Creating second StorageReference.
            StorageReference storageReference2nd = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + GetFileExtension(imageUri));

            // Adding addOnSuccessListener to second StorageReference.
            storageReference2nd.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // Getting image name from EditText and store into string variable.
                            String TempImageName = binding.BookNameEditTxt.getText().toString().trim();
                            String temAuther = binding.TotalBooksEditTxt.getText().toString().trim();
                            String tempBookSub = binding.BookLocationEditTxt.getText().toString().trim();

                            // Hiding the progressDialog after done uploading.
                            progressDialog.dismiss();
                            Log.e("img getName", "" + taskSnapshot.getMetadata().getName());
                            Log.e("img getName", "" + taskSnapshot.getMetadata().getPath());

                            // Showing toast message after done uploading.
                            Toast.makeText(getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();

                            @SuppressWarnings("VisibleForTests")
                            ImageUploadInfo imageUploadInfo = new ImageUploadInfo
                                    (size + 1,tempBookSub, temAuther, TempImageName, taskSnapshot.getMetadata().getName(),
                                            taskSnapshot.getMetadata().getReference().getDownloadUrl().toString(), true, "");

                            // Getting image upload ID.
                            String ImageUploadId = databaseReference.push().getKey();

                            // Adding image upload id s child element into databaseReference.
                            assert ImageUploadId != null;
                            databaseReference.child(ImageUploadId).setValue(imageUploadInfo);
                            Log.e("img getName", "finisg");
                            finish();
                        }
                    })
                    // If something goes wrong .
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            // Hiding the progressDialog.
                            progressDialog.dismiss();

                            // Showing exception erro message.
                            Toast.makeText(AddBookActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })

                    // On progress change upload time.
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            // Setting progressDialog Title.
                            progressDialog.setTitle("Image is Uploading...");

                        }
                    });
        } else {

            Toast.makeText(AddBookActivity.this, "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();

        }
    }
}