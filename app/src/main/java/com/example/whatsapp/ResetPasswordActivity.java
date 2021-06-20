package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.whatsapp.databinding.ActivityResetPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {


    ActivityResetPasswordBinding binding;
    // Declare an instance of FirebaseAuth
    FirebaseAuth auth;
    // Declare an instance of ProgressDialog box
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Binding is used instead of findViewById
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Here if user is not connected to the internet then no internet dialog box is shown
        if (!checkInternet())
            return;


        // Initialize the FirebaseAuth instance
        auth = FirebaseAuth.getInstance();


        // Initialize the ProgressDialog instance
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Reset Password");
        progressDialog.setMessage("Sending reset password link to your email");


        // Following code shows what happens when we click on "Reset Password" button
        // When we click on "Reset Password" button it will send reset password link to the email address of the user
        binding.btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Here if user is not connected to the internet then no internet dialog box is shown
                if (!checkInternet())
                    return;

                // Here if email is empty it will show the error message
                else if (binding.resetPasswordEmail.getText().toString().isEmpty())
                    Toast.makeText(ResetPasswordActivity.this, "Email cannot be empty", Toast.LENGTH_SHORT).show();

                else {

                    // Showing ProgressDialog box whenever "Reset Password" button is clicked
                    progressDialog.show();

                    // Authenticate the user email and sending password reset email link to the user's email address
                    auth.sendPasswordResetEmail(binding.resetPasswordEmail.getText().toString().trim())
                            .addOnCompleteListener(ResetPasswordActivity.this, new OnCompleteListener<Void>() {
                                // After authenticating the user email and sending password reset email link it will publish the result in this method
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    // Closing the ProgressDialog box after authentication is completed
                                    progressDialog.dismiss();

                                    // Here if authentication of user email and sending password reset email link is successful then it will show following message to the user
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ResetPasswordActivity.this, "Reset password link sent to your email", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }

                                    // Here if authentication fails it will show following error message to the user
                                    else
                                        Toast.makeText(ResetPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                }
                            });

                }

            }
        });

    }



    // This method is used to show dialog box when user is not connected to mobile internet or wifi
    private boolean checkInternet() {

        // The following code is used in the dialog box if internet is not connected
        // Initialize connectivityManager
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get active network info
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        // Check network status
        // Here it will show no internet dialog box if internet is inactive and it will return false:
        // networkInfo == null                                         It means networkInfo is having null value
        // !networkInfo.isConnected()                                  It means network is not connected
        // !networkInfo.isAvailable()                                  It means network is not available
        // networkInfo.getType() != ConnectivityManager.TYPE_WIFI      It means wifi is not connected
        if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {

            // Initialize dialog
            Dialog dialog = new Dialog(this);

            // Set content view
            dialog.setContentView(R.layout.sample_no_internet_dialog);

            // Set outside touch
            dialog.setCanceledOnTouchOutside(false);

            // Set dialog width and height
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            // Set transparent background
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // Set animation
            dialog.getWindow().getAttributes().windowAnimations = R.style.Animation_Design_BottomSheetDialog;

            // Initialize button inside dialog box
            Button btnTryAgain = dialog.findViewById(R.id.btnTryAgain);

            // Perform on click listener on above button "btnTryAgain"
            btnTryAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Call recreate method
                    // If internet connection is off this button won't work whenever clicked
                    // If internet connection is on this button will work dialog box will be closed and it will resume the process of app whenever clicked
                    recreate();
                }
            });

            // Show dialog box
            dialog.show();

            return false;

        }

        // If internet is active then it will return true
        else
            return true;
    }

}
