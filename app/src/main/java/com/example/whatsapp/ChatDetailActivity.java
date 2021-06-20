package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.whatsapp.Adapters.MessagesAdapter;
import com.example.whatsapp.Models.MessagesModel;
import com.example.whatsapp.databinding.ActivityChatDetailBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ChatDetailActivity extends AppCompatActivity {


    ActivityChatDetailBinding binding;
    // Declare an instance of FirebaseAuth
    private FirebaseAuth auth;
    // Declare an instance of FirebaseDatabase
    private FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Binding used instead of findViewById
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Initialize the FirebaseAuth instance
        auth = FirebaseAuth.getInstance();


        // Initialize the FirebaseDatabase instance
        database = FirebaseDatabase.getInstance();


        // Setting time when user sends or receive the message
        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat format = new SimpleDateFormat("HH:mm");


        // Here we are getting the value through intent that is passed from user adapter
        final String senderId = auth.getUid();
        final String receiverId = getIntent().getStringExtra("userId");
        String profileImage = getIntent().getStringExtra("profileImage");
        String userName = getIntent().getStringExtra("userName");


        // Setting the above data on the views of the "ChatDetailActivity"
        binding.userName.setText(userName);
        Picasso.get()
                .load(profileImage)
                .placeholder(R.drawable.avatar)
                .into(binding.profileImage);


        // Following code shows what happens when we click on "<-" button
        // When we click on "<-" button it will move to previous activity
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveToMainActivity = new Intent(ChatDetailActivity.this, MainActivity.class);
                moveToMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(moveToMainActivity);
            }
        });


        // Declaring and initializing instance of ArrayList
        final ArrayList<MessagesModel> list = new ArrayList<>();


        // Setting adapter on the recycler view
        final MessagesAdapter adapter = new MessagesAdapter(list, this, receiverId);
        binding.chatDetailRecyclerView.setAdapter(adapter);


        // Vertical scrolling using linear layout manager
        // Setting linear layout manager on the recycler view
        // Here linear layout manager is used to vertically scroll all the items of the recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatDetailRecyclerView.setLayoutManager(layoutManager);


        // Here we are getting all user messages from the database and showing them in the recycler view
        database.getReference().child("Chats")
                .child(senderId + receiverId)
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        list.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            MessagesModel messagesModel = snapshot1.getValue(MessagesModel.class);
                            messagesModel.setMessageId(snapshot1.getKey());
                            list.add(messagesModel);
                        }
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ChatDetailActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }

                });


        // Following code shows what happens when we click on "send" button
        // When we click on "send" button it will send the message
        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // If message is empty then it will run following condition
                if (binding.enterMessage.getText().toString().isEmpty())
                    return;

                else {

                    String message = binding.enterMessage.getText().toString();
                    final MessagesModel model = new MessagesModel(senderId, message);
                    model.setTimestamp(new Date().getTime());
                    model.setTime(format.format(calendar.getTime()));

                    // After message is sent then message box will be empty
                    binding.enterMessage.setText("");

                    // Storing message on sender side and receiver side in the firebase database
                    // Here first we are storing message on sender side in the firebase database
                    database.getReference().child("Chats")
                            .child(senderId + receiverId)
                            .push()
                            .setValue(model)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                // If message is stored in firebase database successfully then this method is called
                                @Override
                                public void onSuccess(Void aVoid) {

                                    // Here we are storing message on receiver side in the firebase database
                                    database.getReference().child("Chats")
                                            .child(receiverId + senderId)
                                            .push()
                                            .setValue(model)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                // If message is stored in firebase database successfully then this method is called
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        // If any error has occurred while storing message in firebase database then this method is called
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ChatDetailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        // If any error has occurred while storing message in firebase database then this method is called
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChatDetailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }

            }
        });

    }


    // When back button of the device is pressed it will move to previous activity
    @Override
    public void onBackPressed() {

        Intent moveToMainActivity = new Intent(ChatDetailActivity.this, MainActivity.class);
        moveToMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(moveToMainActivity);

    }

}
