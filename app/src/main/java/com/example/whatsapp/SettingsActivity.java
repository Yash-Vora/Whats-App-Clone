package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.whatsapp.Models.UserModel;
import com.example.whatsapp.databinding.ActivitySettingsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {


    ActivitySettingsBinding binding;
    // Declare an instance of FirebaseAuth
    private FirebaseAuth auth;
    // Declare an instance of FirebaseDatabase
    private FirebaseDatabase database;
    // Declare an instance of FirebaseStorage
    private FirebaseStorage storage;
    // Declare an instance of ProgressDialog box
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Binding is used instead of findViewById
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Initialize the FirebaseAuth instance
        auth = FirebaseAuth.getInstance();


        // Initialize the FirebaseDatabase instance
        database = FirebaseDatabase.getInstance();


        // Initialize the FirebaseStorage instance
        storage = FirebaseStorage.getInstance();


        // Initialize the ProgressDialog instance
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");


        // Following code shows what happens when we click on "<-" button
        // When we click on "<-" button it will move to previous activity
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveToMainActivity = new Intent(SettingsActivity.this, MainActivity.class);
                moveToMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(moveToMainActivity);
            }
        });


        // Here we are getting current logged in user data from the database and showing them in this activity
        database.getReference().child("Users")
                .child(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        UserModel model = snapshot.getValue(UserModel.class);

                        Picasso.get()
                                .load(model.getProfilePic())
                                .placeholder(R.drawable.profile_image_icon)
                                .into(binding.profileImage);
                        binding.username.setText(model.getUserName());
                        binding.status.setText(model.getStatus());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SettingsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }

                });


        // Following code shows what happens when we click on "+" button
        // When we click on "+" button then user will be redirected to the gallery to select the image
        binding.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent moveToMobileStorage = new Intent();
                moveToMobileStorage.setAction(Intent.ACTION_GET_CONTENT);
                moveToMobileStorage.setType("image/*");
                startActivityForResult(moveToMobileStorage, 30);

            }
        });


        // Following code shows what happens when we click on "save" button
        // When we click on "save" button then user data will be stored in firebase database
        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // If username and status is empty then following error message is shown to the user
                if (binding.username.getText().toString().isEmpty() || binding.status.getText().toString().isEmpty())
                    Toast.makeText(SettingsActivity.this, "Username and Status cannot be empty", Toast.LENGTH_SHORT).show();

                else {

                    // Hash map is used to update values in the database
                    HashMap<String , Object> obj = new HashMap<>();
                    obj.put("userName" , binding.username.getText().toString());
                    obj.put("status" , binding.status.getText().toString());

                    // Storing updated hash map values in the firebase database
                    database.getReference().child("Users")
                            .child(auth.getUid())
                            .updateChildren(obj);

                    Toast.makeText(SettingsActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }


    // This method will be called when user click on "+" button then select any image from the gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (data.getData() != null) {

            // Showing progress dialog box
            progressDialog.show();

            // Getting uri of image that user has selected
            Uri sFile = data.getData();

            // Storing firebase storage reference
            final StorageReference reference = storage.getReference().child("Profile Pictures").child(auth.getUid());

            // Storing image in firebase storage
            reference.putFile(sFile)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        // If image is stored successfully then following message is shown to the user
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // Getting the url of the logged in user
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                // If uri is downloaded successfully then following method is called
                                @Override
                                public void onSuccess(Uri uri) {

                                    // Closing progress dialog box
                                    progressDialog.dismiss();

                                    // Storing image in firebase database
                                    database.getReference().child("Users")
                                            .child(auth.getUid())
                                            .child("profilePic")
                                            .setValue(uri.toString());

                                    Toast.makeText(SettingsActivity.this, "Profile Picture Updated", Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                // If any error occurs while downloading the uri then following message is shown to the user
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });

                            // Showing image to the user in this activity
                            binding.profileImage.setImageURI(sFile);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                // If any error occurs while storing the image then following message is shown to the user
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }

    }


    // When back button of the device is pressed it will move to previous activity
    @Override
    public void onBackPressed() {

        Intent moveToMainActivity = new Intent(SettingsActivity.this, MainActivity.class);
        moveToMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(moveToMainActivity);

    }

}
