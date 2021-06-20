package com.example.whatsapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.whatsapp.Adapters.UserAdapter;
import com.example.whatsapp.Models.UserModel;
import com.example.whatsapp.databinding.FragmentChatsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatsFragment extends Fragment {


    FragmentChatsBinding binding;
    // Declare an instance of ArrayList
    ArrayList<UserModel> list;
    // Declare an instance of FirebaseDatabase
    FirebaseDatabase database;

    // Required empty public constructor
    public ChatsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(inflater, container, false);


        // Initialize the FirebaseDatabase instance
        database = FirebaseDatabase.getInstance();


        // Initialize the ArrayList instance
        list = new ArrayList<>();


        // Setting adapter on the recycler view
        UserAdapter adapter = new UserAdapter(list, getContext());
        binding.chatFragmentRecyclerView.setAdapter(adapter);


        // Vertical scrolling using linear layout manager
        // Setting linear layout manager on the recycler view
        // Here linear layout manager is used to vertically scroll all the items of the recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.chatFragmentRecyclerView.setLayoutManager(layoutManager);


        // Here we are getting all user from the database and showing them in the recycler view
        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    UserModel users = dataSnapshot.getValue(UserModel.class);
                    users.setUserId(dataSnapshot.getKey());

                    // If current user who is logged in the app then that user sample will not be shown in the recycler view
                    if(!users.getUserId().equals(FirebaseAuth.getInstance().getUid()))
                        list.add(users);

                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });


        return binding.getRoot();

    }

}
