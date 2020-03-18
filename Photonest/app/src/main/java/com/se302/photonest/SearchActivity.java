package com.se302.photonest;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import Utils.BottomNavigationViewHelper;

public class SearchActivity extends AppCompatActivity {

    private static final int ACTIVITY_NUM = 1;

    private Context myContext = SearchActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupBottomNavBar();
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
