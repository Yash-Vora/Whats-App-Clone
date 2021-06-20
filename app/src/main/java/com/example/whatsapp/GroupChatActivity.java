package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.whatsapp.Adapters.MessagesAdapter;
import com.example.whatsapp.Models.MessagesModel;
import com.example.whatsapp.databinding.ActivityGroupChatBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GroupChatActivity extends AppCompatActivity {


    ActivityGroupChatBinding binding;
    // Declare an instance of FirebaseAuth
    private FirebaseAuth auth;
    // Declare an instance of FirebaseDatabase
    private FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // binding is used instead of findViewById
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the FirebaseAuth instance
        auth = FirebaseAuth.getInstance();


        // Initialize the FirebaseDatabase instance
        database = FirebaseDatabase.getInstance();


        // Setting time when user sends or receive the message
        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat format = new SimpleDateFormat("HH:mm");


        // Following code shows what happens when we click on "<-" button
        // When we click on "<-" button it will move to previous activity
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // Declaring and initializing instance of ArrayList
        final ArrayList<MessagesModel> list = new ArrayList<>();


        // Setting adapter on the recycler view
        final MessagesAdapter adapter = new MessagesAdapter(list, this);
        binding.chatDetailRecyclerView.setAdapter(adapter);


        // Vertical scrolling using linear layout manager
        // Setting linear layout manager on the recycler view
        // Here linear layout manager is used to vertically scroll all the items of the recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatDetailRecyclerView.setLayoutManager(layoutManager);


        // Setting the title of the group chat
        binding.userName.setText("Friends Group");


        // Storing current user id who is sending the message
        final String senderId = auth.getUid();


        // Here we are getting all user messages from the database and showing them in the recycler view
        database.getReference().child("Group Chat")
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            MessagesModel model = dataSnapshot.getValue(MessagesModel.class);
                            list.add(model);
                        }
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(GroupChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                });


        // Following code shows what happens when we click on "send" button
        // When we click on "send" button it will send the message
        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = binding.enterMessage.getText().toString();
                final MessagesModel model = new MessagesModel(senderId, message);
                model.setTimestamp(new Date().getTime());
                model.setTime(format.format(calendar.getTime()));

                // If message is empty and user click on send button then it won't send a message
                if (message.isEmpty())
                    return;

                else {

                    // After message is sent then message box will be empty
                    binding.enterMessage.setText("");

                    // Storing message in the firebase database
                    database.getReference().child("Group Chat")
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
                            Toast.makeText(GroupChatActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }

            }
        });

    }
    
}
