package com.example.whatsapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.ChatDetailActivity;
import com.example.whatsapp.Models.UserModel;
import com.example.whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

// Here we are extending viewHolder class created inside this class
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewHolder> {


    ArrayList<UserModel> list;
    Context context;


    // Constructor
    public UserAdapter(ArrayList<UserModel> list, Context context) {
        this.list = list;
        this.context = context;
    }


    // This method is used to inflate sample layout file into "fragment_chats.xml" layout file
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_show_user, parent, false);
        return new viewHolder(view);
    }

    // This method is used to set data of all views by it's position in "fragment_chats.xml"
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        final UserModel userModel = list.get(position);

        Picasso.get()
                .load(userModel.getProfilePic())
                .placeholder(R.drawable.avatar)
                .into(holder.profileImage);
        holder.userName.setText(userModel.getUserName());

        // Showing last message to the user
        FirebaseDatabase.getInstance().getReference().child("Chats")
                .child(FirebaseAuth.getInstance().getUid() + userModel.getUserId())
                .orderByChild("timestamp")
                .limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.hasChildren()) {

                            holder.lastMessage.setVisibility(View.VISIBLE);

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                holder.lastMessage.setText(dataSnapshot.child("message").getValue().toString());
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                    }

                });

        // Following is the code shows what happens when we click any of the item of ChatsFragment
        // Here when we click on any of the item it will move to ChatDetailActivity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveToChatDetailActivity = new Intent(context, ChatDetailActivity.class);
                moveToChatDetailActivity.putExtra("userId", userModel.getUserId());
                moveToChatDetailActivity.putExtra("profileImage", userModel.getProfilePic());
                moveToChatDetailActivity.putExtra("userName", userModel.getUserName());
                context.startActivity(moveToChatDetailActivity);
            }
        });

    }

    // This method is used to set size of recycler view in "fragment_chats.xml"
    @Override
    public int getItemCount() {
        return list.size();
    }


    // This class is used to bind all the views that are used in layout file and get that views by it's id
    public class viewHolder extends RecyclerView.ViewHolder {

        ImageView profileImage;
        TextView userName, lastMessage;

        // Constructor
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImage);
            userName = itemView.findViewById(R.id.userName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
        }

    }

}
