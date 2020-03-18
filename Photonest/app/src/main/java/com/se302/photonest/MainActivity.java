package com.se302.photonest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class MainActivity extends AppCompatActivity {
    private ViewPager myViewPager;
    private FrameLayout myFrameLayout;
    private RelativeLayout myRelativeLayout;
    private RelativeLayout myPhotonestBanner;

    private Context myContext = MainActivity.this;

    private static final int ACTIVITY_NUM = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        myFrameLayout = (FrameLayout) findViewById(R.id.container);
        myRelativeLayout = (RelativeLayout) findViewById(R.id.relativeParent);
        myPhotonestBanner = (RelativeLayout) findViewById(R.id.relativeTop);

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
