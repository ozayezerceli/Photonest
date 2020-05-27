package com.se302.photonest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import DataModels.Like;
import DataModels.UserInformation;
import Utils.FirebaseMethods;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionMenuView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.se302.photonest.Model.FollowersActivity;
import com.squareup.picasso.Picasso;


import DataModels.PhotoInformation;

import Utils.BottomNavigationViewHelper;
import Utils.GlideImageLoader;
import Utils.GridImageAdapter;
import Utils.StringManipulation;


import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private TextView username;
    private static final int ACTIVITY_NUM = 4;
    private FirebaseMethods mFirebaseMethods;
    private Context myContext = ProfileActivity.this;
    private String userID;
    private DatabaseReference user_info_ref;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "ProfileActivity";


   private ImageView image_profile;
    private TextView posts, followers,following, fullname, bio, website_link;
   private Button edit_profile;
   private FirebaseUser firebaseUser;
   private String profileid;
   private RelativeLayout mrelativeTop;
    private GridView mGridView;
    private FirebaseMethods firebaseMethods;
    private RelativeLayout mrelativelayout;

    private Uri ImageUri;


    private   ImageButton my_photos;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_profile);
        username = findViewById(R.id.usernameTxt);
        image_profile= findViewById(R.id.profile_image);
        posts = findViewById(R.id.posts);
        followers = findViewById(R.id.followers);
        following = findViewById(R.id.following);
        fullname = findViewById(R.id.fullname_profile);
        bio= findViewById(R.id.bio_profile);
        website_link = findViewById(R.id.website_link_profile);
        edit_profile=findViewById(R.id.edit_profile_button);
        mGridView = findViewById(R.id.grid_view_profile);

        mFirebaseMethods = new FirebaseMethods(ProfileActivity.this);

        firebaseMethods = new FirebaseMethods(ProfileActivity.this);
        mrelativelayout = findViewById(R.id.relativeTop);



        ActionMenuView actionMenuView = findViewById(R.id.profile_menu_view);
        Menu bottomMenu = actionMenuView.getMenu();
        getMenuInflater().inflate(R.menu.profile_menu, bottomMenu);
        setupBottomNavBar();
        setUserPhotos();


        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        user_info_ref= FirebaseDatabase.getInstance().getReference().child("Users");

        for (int i = 0; i < bottomMenu.size(); i++){
            bottomMenu.getItem(i).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.profile_logout:
                            mAuth.signOut();
                            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                            break;
                        case R.id.profile_change_password:
                            Intent i = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
                            startActivity(i);
                            finish();
                            break;
                        case R.id.profile_delete_account:
                            mFirebaseMethods.deleteUserAccount();
                            break;
                    }
                    return false;
                }
            });
        }
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs= myContext.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        profileid = prefs.getString("id" ,  FirebaseAuth.getInstance().getCurrentUser().getUid());



        getFollowers();
        getNrPosts();
        showData();

        if(profileid.equals((firebaseUser.getUid()))){
            edit_profile.setText("EDIT PROFILE");
        }


        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = edit_profile.getText().toString();

                if(btn.equals("EDIT PROFILE"))
                {

                   mrelativeTop=findViewById(R.id.relativeTop);
                   mrelativeTop.setVisibility(View.INVISIBLE);


                    FragmentTransaction transaction = getSupportFragmentManager()
                            .beginTransaction();
                    transaction.replace(R.id.container_edit, new EditProfileFragment());
                    transaction.commit();


                    } else if(btn.equals("Follow")){

                    new FirebaseMethods(ProfileActivity.this).addFollowingAndFollowers(userID); //

                } else if(btn.equals("Following")){

                  new FirebaseMethods((ProfileActivity.this)).removeFollowingAndFollowers(userID);
                }
            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(myContext, FollowersActivity.class);
                intent.putExtra("id",firebaseUser.getUid());
                intent.putExtra("title", "followers");
                startActivity(intent);
            }
        });
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(myContext, FollowersActivity.class);
                intent.putExtra("id", firebaseUser.getUid());
                intent.putExtra("title", "following");
                startActivity(intent);
            }
        });





    }



    private  void getFollowers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followers.setText(""+firebaseMethods.getFollowerCount(dataSnapshot, userID));
                following.setText(""+firebaseMethods.getFollowingCount(dataSnapshot, userID));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

  private void getNrPosts(){
      DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
      reference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            posts.setText(""+firebaseMethods.getImageCount(dataSnapshot));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
}


    private void showData() {

        user_info_ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String UserName = dataSnapshot.child("username").getValue().toString();
                            String FullName = dataSnapshot.child("fullName").getValue().toString();
                            String E_Mail = dataSnapshot.child("email").getValue().toString();
                            String BIO = dataSnapshot.child("bio").getValue().toString();
                            String website = dataSnapshot.child("website_link").getValue().toString();
                            String Image_Url = dataSnapshot.child("imageurl").getValue().toString();

                            GlideImageLoader.loadImageWithOutTransition(myContext, Image_Url, image_profile);

                            username.setText(UserName);
                            fullname.setText(FullName);
                            bio.setText(BIO);
                            website_link.setText(website);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void setUserPhotos(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.dbname_user_photos)).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ArrayList<PhotoInformation> photoArrayList = new ArrayList<PhotoInformation>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
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

                }
                Collections.reverse(photoArrayList);
                int gridWidth = getResources().getDisplayMetrics().widthPixels;
                int imageWidth = gridWidth/3;
                mGridView.setColumnWidth(imageWidth);

                ArrayList<String> imgUrls = new ArrayList<String>();
                for(int i = 0; i < photoArrayList.size(); i++){
                    imgUrls.add(photoArrayList.get(i).getImage_path());


                }
                GridImageAdapter adapter = new GridImageAdapter(ProfileActivity.this , R.layout.grid_imageview, "", imgUrls);
                mGridView.setAdapter(adapter);

                mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mrelativelayout.setVisibility(View.INVISIBLE);
                        PostViewFragment post_view_fragment = new PostViewFragment();
                        Bundle args = new Bundle();
                        args.putParcelable("photo", photoArrayList.get(position));
                        args.putInt("activityNumber", 1);
                        post_view_fragment.setArguments(args);

                        FragmentTransaction transaction  = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.container_edit, post_view_fragment);
                        transaction.addToBackStack("View Post");
                        transaction.commit();
                    }
                });
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
        MenuItem mItem = menu.getItem(ACTIVITY_NUM);
        mItem.setChecked(true);
    }


}
