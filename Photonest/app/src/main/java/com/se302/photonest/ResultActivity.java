package com.se302.photonest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.List;

import DataModels.PhotoInformation;
import Utils.BottomNavigationViewHelper;
import Utils.GlideImageLoader;
import Utils.GridImageAdapter;
import Utils.StringManipulation;

public class ResultActivity extends AppCompatActivity {

    private String hashTags;
    private GridView mGridView;
    private RelativeLayout mRelative;
    private ImageView mBackArrow,mContainer;
    private TextView mText;
    private static final int ACTIVITY_NUM = 1;

    @SuppressLint("ResourceType")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        mGridView = findViewById(R.id.grid_view_result);
        mContainer = findViewById(R.id.empty_container_result);
        mRelative = findViewById(R.id.relativeParent_result);
        mBackArrow = findViewById(R.id.backArrow_result);
        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mText = findViewById(R.id.result_hash_txt);

        Intent mediaIntent = getIntent();
        hashTags = mediaIntent.getStringExtra("hashTags");

        mText.setText("#"+hashTags);
        setupBottomNavBar();
        setHashPhotos();
    }

    private void setHashPhotos() {
        final ArrayList<PhotoInformation> photoArrayList = new ArrayList<PhotoInformation>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("hashTags").child(hashTags);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    String photoid = snap.getKey();
                    Query query = FirebaseDatabase.getInstance().getReference().child("dbname_photos").child(photoid);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                            PhotoInformation photoInformation = new PhotoInformation();
                            photoInformation.setCaption(snapshot.child("caption").getValue().toString());
                            photoInformation.setPhoto_id(snapshot.child("photo_id").getValue().toString());
                            photoInformation.setUser_id(snapshot.child("user_id").getValue().toString());
                            List<String> hashTags = StringManipulation.getHashTags(photoInformation.getCaption());
                            photoInformation.setHashTags(hashTags);
                            photoInformation.setDate_created(snapshot.child("date_created").getValue().toString());
                            photoInformation.setImage_path(snapshot.child("image_path").getValue().toString());
                                if(snapshot.child("location").getValue().toString().length()>=1) {
                                    photoInformation.setLocation(snapshot.child("location").getValue().toString());
                                }else{
                                    photoInformation.setLocation("Location Unknown");
                                }
                            photoArrayList.add(photoInformation);

                        }else{
                                int gridWidth = getResources().getDisplayMetrics().widthPixels;
                                int imageWidth = gridWidth / 3;
                                mGridView.setColumnWidth(imageWidth);
                                if(photoArrayList.size()>0){
                                    ArrayList<String> imgUrls = new ArrayList<String>();
                                    for (int i = 0; i < photoArrayList.size(); i++) {
                                        imgUrls.add(photoArrayList.get(i).getImage_path());
                                    }

                                    GridImageAdapter adapter = new GridImageAdapter(ResultActivity.this, R.layout.grid_imageview, "", imgUrls);
                                    mGridView.setAdapter(adapter);

                                    mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            mRelative.setVisibility(View.INVISIBLE);
                                            PostViewFragment post_view_fragment = new PostViewFragment();
                                            Bundle args = new Bundle();
                                            args.putParcelable("photo", photoArrayList.get(position));
                                            args.putInt("activityNumber", 1);
                                            post_view_fragment.setArguments(args);

                                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                            transaction.replace(R.id.container_result, post_view_fragment);
                                            transaction.commit();
                                        }
                                    });
                                }else{
                                    mContainer.setImageResource(R.drawable.empty_photo_result);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void setupBottomNavBar() {
        BottomNavigationViewEx bottomNavBar = (BottomNavigationViewEx) findViewById(R.id.bottomNavBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavBar);
        BottomNavigationViewHelper.enableNavigation(ResultActivity.this, this, bottomNavBar);
        Menu menu = bottomNavBar.getMenu();
        MenuItem mItem = menu.getItem(ACTIVITY_NUM);
        mItem.setChecked(true);
    }
}
