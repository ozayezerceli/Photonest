package com.se302.photonest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import Utils.GlideImageLoader;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionMenuView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.theartofdev.edmodo.cropper.CropImageView;

import DataModels.PhotoInformation;
import DataModels.UserInformation;
import Utils.BottomNavigationViewHelper;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private TextView username;
    private static final int ACTIVITY_NUM = 4;
    private Context myContext = ProfileActivity.this;
    private String userID;
    private DatabaseReference user_info_ref;

   private ImageView image_profile;
    private TextView posts, followers,following, fullname, bio, website_link;
   private Button edit_profile;
   private FirebaseUser firebaseUser;
   private String profileid;
   private RelativeLayout mrelativeTop;

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
        my_photos= findViewById(R.id.my_photos);






        ActionMenuView actionMenuView = findViewById(R.id.profile_menu_view);
        Menu bottomMenu = actionMenuView.getMenu();
        getMenuInflater().inflate(R.menu.profile_menu, bottomMenu);
        setupBottomNavBar();


      /*  image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galeriIntent= new Intent();
                galeriIntent.setAction(Intent.ACTION_GET_CONTENT);
                galeriIntent.setType("image/*");
                startActivityForResult(galeriIntent,GaleriPick);

            }
        }); */

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
                            user_info_ref= FirebaseDatabase.getInstance().getReference().child("Users");
                            if(user!=null){
                                new AlertDialog.Builder(ProfileActivity.this)
                                        .setTitle("Delete")
                                        .setMessage("Your account will be deleted. \nAre you sure?")
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                user_info_ref.child(user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    Toast.makeText(ProfileActivity.this, "Account deleted", Toast.LENGTH_LONG).show();
                                                                    Intent i = new Intent(ProfileActivity.this,LoginActivity.class);
                                                                    startActivity(i);
                                                                    finish();
                                                                } else{
                                                                    Toast.makeText(ProfileActivity.this, "Account could not be deleted", Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }})
                                        .setNegativeButton(android.R.string.no, null).show();
                            }
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
        } else{
            checkFollow();
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
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("Following").child(userID).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(userID)
                            .child("Followers").child(firebaseUser.getUid()).setValue(true);

                } else if(btn.equals("Following")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("Following").child(userID).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(userID)
                            .child("Followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });


    }



    private  void checkFollow(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(userID).exists()){
                    edit_profile.setText("Following");
                } else {
                    edit_profile.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private  void getFollowers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(userID).child("Followers");

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
                .child("Follow").child(userID).child("Following");

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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("photos");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i =0;
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    PhotoInformation post= snapshot.getValue(PhotoInformation.class);
                    if(post.getPublisher().equals(userID)){ //getPublisher() doldurulacak
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


                        //    Uri uri_pp= Uri.parse("R.drawable.place_holder_photo");
                     //       GlideImageLoader.loadImageWithOutTransition(myContext,Image_Url,image_profile);
                            Glide.with(getApplicationContext()).load(Image_Url).into(image_profile);


                            username.setText(UserName);
                            fullname.setText(FullName);
                            bio.setText(BIO);
                            website_link.setText(website);
                            //   Glide.with(getApplicationContext()).load(Image_Url).into(image_profile);

                        }
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
