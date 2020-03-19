package com.se302.photonest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import Utils.BottomNavigationViewHelper;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final int ACTIVITY_NUM = 4;
    Button logoutButton;
    private Context myContext = ProfileActivity.this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        logoutButton = findViewById(R.id.profile_logout_button);
        setupBottomNavBar();
        mAuth = FirebaseAuth.getInstance();
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupBottomNavBar(){
        BottomNavigationViewEx bottomNavBar = (BottomNavigationViewEx) findViewById(R.id.bottomNavBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavBar);
        BottomNavigationViewHelper.enableNavigation(myContext, this, bottomNavBar);
        Menu menu = bottomNavBar.getMenu();
        MenuItem mItem = menu.getItem(ACTIVITY_NUM);
        mItem.setChecked(true);
    }
}
