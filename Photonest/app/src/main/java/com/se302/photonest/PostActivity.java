package com.se302.photonest;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.Objects;

import Utils.SectionsPagerAdapter;
import Utils.Permissions;


public class PostActivity extends AppCompatActivity{

    private static final int VERIFY_PERMISSIONS_REQUEST = 1;

    private ViewPager ViewPager;


    private Context Context = PostActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        if(checkPermissionsArray(Permissions.PERMISSIONS)){
            setupViewPager();
        }else{
            verifyPermissions(Permissions.PERMISSIONS);
            finish();
        }

    }

    public int getCurrentTabNumber(){
        return ViewPager.getCurrentItem();
    }

    private void setupViewPager(){
        SectionsPagerAdapter adapter =  new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PhotoFragment());
        adapter.addFragment(new GalleryFragment());

        ViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        ViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsBottom);
        tabLayout.setupWithViewPager(ViewPager);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setText(getString(R.string.camera));
        Objects.requireNonNull(tabLayout.getTabAt(1)).setText(getString(R.string.gallery));

    }


    public void verifyPermissions(String[] permissions){

        ActivityCompat.requestPermissions(
                PostActivity.this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }

    public boolean checkPermissionsArray(String[] permissions){

        for(int i = 0; i< permissions.length; i++){
            String check = permissions[i];
            if(!checkPermissions(check)){
                return false;
            }
        }
        return true;
    }

    public boolean checkPermissions(String permission){

        int permissionRequest = ActivityCompat.checkSelfPermission(PostActivity.this, permission);

        if(permissionRequest != PackageManager.PERMISSION_GRANTED){
            return false;
        }
        else{
            return true;
        }
    }
}
