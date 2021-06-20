package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.whatsapp.Models.UserModel;
import com.example.whatsapp.databinding.ActivitySignInBinding;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class SignInActivity extends AppCompatActivity {


    ActivitySignInBinding binding;
    // Declare an instance of FirebaseAuth
    private FirebaseAuth auth;
    // Declare an instance of FirebaseDatabase
    private FirebaseDatabase database;
    // Declare an instance of ProgressDialog box
    private ProgressDialog progressDialog, googleProgressDialog, facebookProgressDialog;
    // Declare an instance of GoogleSignInClient
    private GoogleSignInClient mGoogleSignInClient;
    // Declare an instance of CallbackManager
    private CallbackManager mCallbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Binding is used instead of findViewById
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Following code shows what happens when we click on "Reset Password"
        // When we click on "Reset Password" it will move to "ResetPasswordActivity" screen
        binding.resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, ResetPasswordActivity.class));
            }
        });


        // Following code shows what happens when we click on "Want to create account"
        // When we click on "Already have account" it will move to "SignUpActivity" screen
        binding.moveToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            }
        });


        // Following code shows what happens when we click on "Sign In with Phone"
        // When we click on "Sign Up with Phone" it will move to "PhoneActivity" screen
        binding.signInPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, PhoneActivity.class));
            }
        });


        // Initialize the ProgressDialog instance and it is used when user is signing in using email/password
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Logging into your account");


        // Initialize the ProgressDialog instance and it is used when user is signing in using google
        googleProgressDialog = new ProgressDialog(this);
        googleProgressDialog.setTitle("Login With Google");
        googleProgressDialog.setMessage("Logging into your google account");


        // Initialize the ProgressDialog instance and it is used when user is signing in using facebook
        facebookProgressDialog = new ProgressDialog(this);
        facebookProgressDialog.setTitle("Login With Facebook");
        facebookProgressDialog.setMessage("Logging into your facebook account");


        // Initialize the FirebaseAuth instance
        auth = FirebaseAuth.getInstance();


        // Initialize the FirebaseDatabase instance
        database = FirebaseDatabase.getInstance();


        // Following code shows what happens when we click on "Sign In" button
        // When we click on "Sign In" button it will authenticate the user and sign in user into the app
        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Here if user is not connected to the internet then no internet dialog box is shown
                if (!checkInternet())
                    return;

                // Here if email or password is empty it will show the error message
                else if (binding.signInEmail.getText().toString().isEmpty() || binding.signInPassword.getText().toString().isEmpty())
                    Toast.makeText(SignInActivity.this, "Email or Password cannot be empty", Toast.LENGTH_SHORT).show();

                else {

                    // Showing ProgressDialog box whenever "Sign In" button is clicked
                    progressDialog.show();

                    // Authenticate the user email and password
                    auth.signInWithEmailAndPassword
                            (binding.signInEmail.getText().toString().trim(), binding.signInPassword.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                // After authenticating the user it will publish the result in this method
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    // Closing the ProgressDialog box after authentication is completed
                                    progressDialog.dismiss();

                                    // Here if authentication of user is successfully then it will move to "Main Activity" page
                                    // In authentication it will check user email and password in the database is correct or not. If it's correct then it will sign in the user
                                    if (task.isSuccessful()) {

                                        // If the has verified the email then it will move user to "Main Activity" page
                                        if (auth.getCurrentUser().isEmailVerified()) {

                                            // After authentication is done successfully user will be signed in and it will move to "MainActivity" page
                                            Intent moveToMainActivity = new Intent(SignInActivity.this, MainActivity.class);
                                            moveToMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(moveToMainActivity);

                                        }

                                        // If user has not verified the email then it will show following error message
                                        else
                                            Toast.makeText(SignInActivity.this, "Please verify email link sent to your email address", Toast.LENGTH_LONG).show();

                                    }

                                    // Here if authentication fails it will show following error messages to the user
                                    // If data entered by the user do'nt exists in the database then following error message is shown to the user
                                    else {

                                        // If user is not existing in the database then following error message is shown to the user
                                        if (((FirebaseAuthException) task.getException()).getErrorCode() == "ERROR_USER_NOT_FOUND")
                                            Toast.makeText(SignInActivity.this, "User is not registered", Toast.LENGTH_SHORT).show();

                                        // If email is invalid following error message is shown to the user
                                        else if (task.getException() instanceof FirebaseAuthInvalidUserException)
                                            Toast.makeText(SignInActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();

                                        // If password is invalid following error message is shown to the user
                                        else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                                            Toast.makeText(SignInActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();

                                        // If any other authentication error occurs then following error message is shown to the user
                                        else
                                            Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                    }

                                }
                            });

                }

            }
        });


        // If user is already logged in then it will directly open "Main Activity" page
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish();
        }


        // The following code is used when we are signing in/up through google
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Initialize the GoogleSignInClient instance
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        // Following code shows what happens when we click on "Google" button
        // When we click on "Google" button user can log in through their google account
        binding.signInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });


        // Initialize the CallbackManager instance
        mCallbackManager = CallbackManager.Factory.create();


        // Following code shows what happens when we click on "Facebook" button
        // When we click on "Facebook" button user can log in through their facebook account
        binding.signInFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Here we are giving read permissions like email and public profile for signing in using facebook
                LoginManager.getInstance()
                        .logInWithReadPermissions(SignInActivity.this, Arrays.asList("email", "public_profile"));

                // Here it is registering the callback and returning login result in this method
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

                    // If facebook sign in was successful then following method is called and then it will authenticate will firebase
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    // If user cancel the facebook page then following method is called
                    @Override
                    public void onCancel() {
                        // Facebook cancel request
                    }

                    // If facebook sign in was successful then following method is called and error message is shown to the user
                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(SignInActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }

                });

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



    /* Following are some methods used when user is signing in/up through google */


    // This method is called when we click on google button
    // RC_SIGN_IN is value between 10 to 99
    int RC_SIGN_IN = 65;
    private void signInWithGoogle() {
        // This intent is used to get all gmail sign in accounts of the user and show that data into this activity
        // For getting all gmail sign in accounts of the user we are using onActivityResult() method
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    // This method is called from signInWithGoogle() method to get all gmail sign in accounts of the user and user can also add new account
    // This method is also called when user click on "Facebook" button to get all facebook sign in accounts of the user and user can also add new account
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // If user is signing in using google then following condition will run
        if (requestCode == RC_SIGN_IN) {

            // Here we are getting all gmail sign in accounts of the user and show that data into this activity
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            // Google Sign In was successful, authenticate with Firebase
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            }

            // Google Sign In failed
            catch (ApiException e) {
                // Google sign in failed
            }

        }

        // If user is signing in using facebook then following condition will run
        else {

            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);

        }

    }


    // This method is called from onActivityResult() method it will authenticate the user and store data in the database using firebase
    private void firebaseAuthWithGoogle(String idToken) {

        // Showing ProgressDialog box whenever "Google" button is clicked
        googleProgressDialog.show();

        // Here we are getting token for authenticating the user
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        // Here it will authenticate the user
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    // After authenticating the user it will publish the result in this method
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // Closing the ProgressDialog box after authentication is completed
                        googleProgressDialog.dismiss();

                        // Here if authentication of user is successful that data will be stored into firebase database if it is not stored
                        // If data is already stored in the database then it will just sign in the user
                        if (task.isSuccessful()) {

                            // Storing user data in the firebase database
                            FirebaseUser user = auth.getCurrentUser();
                            UserModel users = new UserModel();
                            users.setUserName(user.getDisplayName());
                            users.setEmail(user.getEmail());
                            users.setProfilePic(user.getPhotoUrl().toString());
                            database.getReference().child("Users").child(user.getUid()).setValue(users);

                            // After authentication is done successfully user will be signed in and it will move to "MainActivity" page
                            Intent moveToMainActivity = new Intent(SignInActivity.this, MainActivity.class);
                            moveToMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(moveToMainActivity);

                        }

                        // Here if authentication fails it will show following error message to the user
                        else
                            Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                    }
                });

    }



    /* Following are some methods used when user is signing in/up through facebook */


    // The another onActivityResult() method is also used while signing in using facebook and it's code is written in above onActivityResult() method


    // This method is called from onActivityResult() method it will authenticate the user and store data in the database using firebase
    private void handleFacebookAccessToken(AccessToken token) {

        // Showing ProgressDialog box whenever "Facebook" button is clicked
        facebookProgressDialog.show();

        // Here we are getting token for authenticating the user
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        // Here it will authenticate the user
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    // After authenticating the user it will publish the result in this method
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // Closing the ProgressDialog box after authentication is completed
                        facebookProgressDialog.dismiss();

                        // Here if authentication of user is successful that data will be stored into firebase database if it is not stored
                        // If data is already stored in the database then it will just sign in the user
                        if (task.isSuccessful()) {

                            // Storing user data in the firebase database
                            FirebaseUser user = auth.getCurrentUser();
                            UserModel users = new UserModel();
                            users.setUserName(user.getDisplayName());
                            users.setEmail(user.getEmail());
                            users.setProfilePic(user.getPhotoUrl().toString());
                            database.getReference().child("Users").child(user.getUid()).setValue(users);

                            // After authentication is done successfully user will be signed in and it will move to "MainActivity" page
                            Intent moveToMainActivity = new Intent(SignInActivity.this, MainActivity.class);
                            moveToMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(moveToMainActivity);

                        }

                        // Here if authentication fails it will show following error message to the user
                        else
                            Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                    }
                });

    }

}
