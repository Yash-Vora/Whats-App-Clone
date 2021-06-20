package com.example.whatsapp.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.Models.MessagesModel;
import com.example.whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

// Here we are extending recyclerView adapter
public class MessagesAdapter extends RecyclerView.Adapter {


    ArrayList<MessagesModel> list;
    Context context;
    String receiverId;

    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;


    // Constructor
    public MessagesAdapter(ArrayList<MessagesModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    // Constructor
    public MessagesAdapter(ArrayList<MessagesModel> list, Context context, String receiverId) {
        this.list = list;
        this.context = context;
        this.receiverId = receiverId;
    }


    // This method is used to inflate sample layout file if it is sender then it will inflate "sample_sender.xml" and if it is receiver then it will inflate "sample_receiver"
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == SENDER_VIEW_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false);
            return new SenderViewHolder(view);
        }

        else {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_receiver, parent, false);
            return new ReceiverViewHolder(view);
        }

    }

    // This method is used to set data of all views by it's position if it is sender then it will set data on "sample_sender.xml" file and if it is receiver then it will set data on "sample_receiver" file
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MessagesModel model = list.get(position);

        if (holder.getClass() == SenderViewHolder.class) {
            ((SenderViewHolder) holder).senderMsg.setText(model.getMessage());
            ((SenderViewHolder) holder).senderTime.setText(model.getTime());
        }

        else {
            ((ReceiverViewHolder) holder).receiverMsg.setText(model.getMessage());
            ((ReceiverViewHolder) holder).receiverTime.setText(model.getTime());
        }

        // Following is the code what happens when we long click on any of the messages of ChatDetailActivity
        // Here when we long click on any of the message it will delete that message
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                // Alert Dialog Box
                // It will be shown when we long press on any of the message
                new AlertDialog.Builder(context)
                        .setTitle("Delete Message")
                        .setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // Deleting message from the firebase database
                                FirebaseDatabase.getInstance().getReference().child("Chats")
                                        .child(FirebaseAuth.getInstance().getUid() + receiverId)
                                        .child(model.getMessageId())
                                        .setValue(null);

                            }
                        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

                return true;

            }
        });

    }

    // This method is used to set size of recycler view in "activity_chat_detail.xml"
    @Override
    public int getItemCount() {
        return list.size();
    }

    // This method is used to check whether the user is sender or receiver
    @Override
    public int getItemViewType(int position) {

        if (list.get(position).getUid().equals(FirebaseAuth.getInstance().getUid()))
            return SENDER_VIEW_TYPE;

        else
            return RECEIVER_VIEW_TYPE;

    }


    // This class is used to bind all the views that are used in layout file "sample_receiver.xml" and get that views by it's id
    public class ReceiverViewHolder extends RecyclerView.ViewHolder {

        TextView receiverMsg, receiverTime;

        // Constructor
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);

            receiverMsg = itemView.findViewById(R.id.receiverText);
            receiverTime = itemView.findViewById(R.id.receiverTime);
        }

    }


    // This class is used to bind all the views that are used in layout file "sample_sender.xml" and get that views by it's id
    public class SenderViewHolder extends RecyclerView.ViewHolder {

        TextView senderMsg, senderTime;

        // Constructor
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMsg = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTime);
        }

    }

}
