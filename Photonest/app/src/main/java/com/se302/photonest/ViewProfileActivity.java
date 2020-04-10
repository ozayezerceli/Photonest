package com.se302.photonest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;


import DataModels.PhotoInformation;
import DataModels.UserInformation;
import Utils.BottomNavigationViewHelper;

public class ViewProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private TextView username;
    private Context myContext = ViewProfileActivity.this;
    private String userID;
    private UserInformation muser;

    private ImageView image_profile;
    private TextView posts, followers,following, fullname, bio;
    private Button follow_Btn, unfollow_Btn, editprofile_Btn;
    private RelativeLayout mrelativelayout;

    ImageButton my_photos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        username = findViewById(R.id.ViewusernameTxt);
        fullname = findViewById(R.id.View_fullname_profile);
        bio = findViewById(R.id.View_bio_profile);
        posts = findViewById(R.id.View_posts);
        followers = findViewById(R.id.View_followers);
        following = findViewById(R.id.View_following);
        follow_Btn = findViewById(R.id.Follow_button);
        unfollow_Btn = findViewById(R.id.UnFollow_button);
        editprofile_Btn = findViewById(R.id.View_editprofile_button);
        image_profile = findViewById(R.id.View_profile_image);
        mrelativelayout = findViewById(R.id.relativeTop);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        FirebaseUser user =mAuth.getCurrentUser();
        userID = user.getUid();

        init();
        getFollowers();
        getNrPosts();
        setupBottomNavBar();

        follow_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "onClick: now following: " + mUser.getUsername());

                FirebaseDatabase.getInstance().getReference()
                        .child("Follow")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("Following")
                        .child(muser.getid())
                        .child("user_id")
                        .setValue(muser.getid());

                FirebaseDatabase.getInstance().getReference()
                        .child("Follow")
                        .child(muser.getid())
                        .child("Followers")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("user_id")
                        .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                setFollowing();
            }
        });


        unfollow_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "onClick: now unfollowing: " + mUser.getUsername());

                FirebaseDatabase.getInstance().getReference()
                        .child("Follow")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("Following")
                        .child(muser.getid())
                        .removeValue();

                FirebaseDatabase.getInstance().getReference()
                        .child("Follow")
                        .child(muser.getid())
                        .child("Followers")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .removeValue();
                setUnfollowing();
            }
        });

        editprofile_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mrelativelayout.setVisibility(View.GONE);
                EditProfileFragment fragment = new EditProfileFragment();
                FragmentTransaction transaction = ViewProfileActivity.this.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment);
                transaction.addToBackStack(getString(R.string.profile_fragment));
                transaction.commit();
            }
        });



        if(muser.getid().equals(userID)){
            follow_Btn.setVisibility(View.GONE);
            unfollow_Btn.setVisibility(View.GONE);
            editprofile_Btn.setVisibility(View.VISIBLE);
        } else{
            checkFollow();
        }
    }

    private void init(){
        Intent intent = getIntent();
        if(intent.hasExtra(getString(R.string.selected_user))){
            Bundle args = new Bundle();
            args.putParcelable(getString(R.string.selected_user),intent.getParcelableExtra(getString(R.string.selected_user)));
            muser = args.getParcelable(getString(R.string.selected_user));
            username.setText(muser.getUsername());
            fullname.setText(muser.getFullName());
            bio.setText(muser.getBio());
            Glide.with(getApplicationContext()).load(muser.getImageurl()).into(image_profile);
        }else{
            Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
        }

    }

    private  void checkFollow(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("Follow").child(userID).child("Following").orderByChild("user_id").equalTo(muser.getid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    //editprofile_Btn.setText("Following");
                    setFollowing();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setFollowing(){
        //Log.d(TAG, "setFollowing: updating UI for following this user");
        follow_Btn.setVisibility(View.GONE);
        unfollow_Btn.setVisibility(View.VISIBLE);
        editprofile_Btn.setVisibility(View.GONE);
    }

    private void setUnfollowing(){
        //Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
        follow_Btn.setVisibility(View.VISIBLE);
        unfollow_Btn.setVisibility(View.GONE);
        editprofile_Btn.setVisibility(View.GONE);
    }

    private  void getFollowers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(muser.getid()).child("Followers");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followers.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(muser.getid()).child("Following");

        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                following.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getNrPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i =0;
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    PhotoInformation post= snapshot.getValue(PhotoInformation.class);
                    if(post.getPublisher().equals(muser.getid())){ //getPublisher() doldurulacak
                        i++;
                    }
                }
                posts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setupBottomNavBar(){
        BottomNavigationViewEx bottomNavBar = (BottomNavigationViewEx) findViewById(R.id.bottomNavBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavBar);
        BottomNavigationViewHelper.enableNavigation(myContext, this, bottomNavBar);
        Menu menu = bottomNavBar.getMenu();
        MenuItem mItem = menu.getItem(1);
        mItem.setChecked(true);
    }
}
