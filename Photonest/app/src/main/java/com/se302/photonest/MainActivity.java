package com.se302.photonest;


import android.content.Context;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;


import Utils.BottomNavigationViewHelper;
import Utils.UniversalImageLoader;

public class MainActivity extends AppCompatActivity {

    private static final int ACTIVITY_NUM = 0;
    private Context mContext = MainActivity.this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initImageLoader();
        setupBottomNavBar();
        Fragment mFragment = null;
        mFragment = new MainFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, mFragment).commit();

    }

    private void setupBottomNavBar(){
        BottomNavigationViewEx bottomNavBar = (BottomNavigationViewEx) findViewById(R.id.bottomNavBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavBar);
        BottomNavigationViewHelper.enableNavigation(mContext, this, bottomNavBar);
        Menu menu = bottomNavBar.getMenu();
        MenuItem mItem = menu.getItem(ACTIVITY_NUM);
        mItem.setChecked(true);
    }


    private void initImageLoader(){

        UniversalImageLoader universalImageLoader = new UniversalImageLoader(MainActivity.this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }




}