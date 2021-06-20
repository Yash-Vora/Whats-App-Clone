package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.whatsapp.Adapters.FragmentAdapter;
import com.example.whatsapp.databinding.ActivityMainBinding;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {


    ActivityMainBinding binding;
    // Declare an instance of FirebaseAuth
    private FirebaseAuth auth;
    // Declare an instance of GoogleSignInClient
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Binding is used instead of findViewById
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Custom Toolbar
        Toolbar customToolbar = binding.mainCustomToolbar.customToolbar;
        // OR
        // Toolbar customToolbar = findViewById(R.id.mainCustomToolbar);
        setSupportActionBar(customToolbar);
        // OR
        // setSupportActionBar(binding.mainCustomToolbar.customToolbar);


        // Initialize the FirebaseAuth instance
        auth = FirebaseAuth.getInstance();


        // Initialize the GoogleSignInClient instance
        mGoogleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);


        // Setting adapter on the view pager
        binding.viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager()));


        // Setting view pager on the tab layout
        binding.tabLayout.setupWithViewPager(binding.viewPager);

    }


    // Menu shown at right side of the toolbar
    // This function is used to show or inflate menu in main activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_menu, menu);
        return true;
    }


    // This function is used to show what will happen after selecting any item from menu that is at right side of toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;

            case R.id.signOut:
                // Sign out from firebase
                // If user has signed in through email/password or phone then then user will move to "SignInActivity" page
                auth.signOut();
                // Sign out from google
                // If user has signed in through google then user will move to "SignInActivity" page and then if user want to again sign in through google user can choose different account
                mGoogleSignInClient.signOut();
                // Sign out from facebook
                // If user has signed in through facebook then user will move to "SignInActivity" page and then if user want to again sign in through facebook user can choose different account
                LoginManager.getInstance().logOut();
                Intent moveToSignInActivity = new Intent(MainActivity.this, SignInActivity.class);
                moveToSignInActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(moveToSignInActivity);
                break;

            case R.id.groupChat:
                startActivity(new Intent(MainActivity.this, GroupChatActivity.class));
                break;

            default:

        }
        return true;

    }

}
